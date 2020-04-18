package org.sonarsource.plugins.example.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

public class MyFooRuleDefinition implements RulesDefinition {

    public static final String REPOSITORY = "foo-example";
    public static final String FOO_LANGUAGE = "foo";
    public static final RuleKey FOO_RULE = RuleKey.of(REPOSITORY, "foo");

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY, FOO_LANGUAGE).setName("My Custom Foo Analyzer");

        NewRule rule = repository.createRule(FOO_RULE.rule())
                .setName("Foo rule")
                .setHtmlDescription("Generates an issue on every foo token occurrence.")

                // optional tags
                .setTags("style", "foo")

                // optional status. Default value is READY.
                .setStatus(RuleStatus.BETA)

                // default severity when the rule is activated on a Quality profile. Default value is MAJOR.
                .setSeverity(Severity.MINOR);

        rule.setDebtRemediationFunction(rule.debtRemediationFunctions().linearWithOffset("1h", "30min"));

        // don't forget to call done() to finalize the definition
        repository.done();
    }
}
