package org.sonarsource.plugins.example.rules;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.example.languages.FooLanguage;
import org.sonarsource.plugins.example.settings.FooLanguageProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public class MyFooSensor implements Sensor {

    private static final Logger LOGGER = Loggers.get(MyFooSensor.class);

    private static final double ARBITRARY_GAP = 2.0;

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name("My Foo Sensor description.");
        sensorDescriptor.onlyOnLanguage(FooLanguage.KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        Iterable<InputFile> fooFiles = fs.inputFiles(fs.predicates().hasLanguage("foo"));
        List<PropertyDefinition> fooLanguageProperties = FooLanguageProperties.getProperties();
        Optional<PropertyDefinition> tokenPropertyOptional = fooLanguageProperties.stream()
                .filter(it -> it.key().equals(FooLanguageProperties.TOKEN_KEY)).findFirst();
        Optional<String> tokenValueOptional = context.config().get(FooLanguageProperties.TOKEN_KEY);
        String tokenDefaultValue = tokenPropertyOptional.map(PropertyDefinition::defaultValue).orElse(null);
        String token = tokenValueOptional.orElse(tokenDefaultValue);

        // even if we have token here, missing default value is a sign that someone changed properties class and something works wrong
        if(token == null || tokenDefaultValue == null) return;

        for (InputFile fooFile : fooFiles) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fooFile.inputStream()));
                String line;
                int lineCounter = 1;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(token)) {
                        int tokenLength = token.length();
                        int startAt = line.indexOf(token);
                        int endsAt = startAt + tokenLength;
                        DefaultTextPointer startAtPointer = new DefaultTextPointer(lineCounter, startAt);
                        DefaultTextPointer endsAtPointer = new DefaultTextPointer(lineCounter, endsAt);
                        DefaultTextRange textRange = new DefaultTextRange(startAtPointer, endsAtPointer);

                        NewIssue newIssue = context.newIssue()
                                .forRule(MyFooRuleDefinition.FOO_RULE)

                                // gap is used to estimate the remediation cost to fix the debt
                                .gap(ARBITRARY_GAP);

                        NewIssueLocation location = newIssue.newLocation()
                                .on(fooFile)
                                .at(textRange)
                                .message("Foo token here!");
                        newIssue.at(location);
                        newIssue.save();
                        lineCounter++;
                    }
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
