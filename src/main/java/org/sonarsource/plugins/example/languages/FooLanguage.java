/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.plugins.example.languages;

import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.AbstractLanguage;
import org.sonarsource.plugins.example.settings.FooLanguageProperties;

import java.util.Optional;

/**
 * This class defines the fictive Foo language.
 */
public final class FooLanguage extends AbstractLanguage {

  public static final String NAME = "Foo";
  public static final String KEY = "foo";

  private final Configuration config;

  public FooLanguage(Configuration config) {
    super(KEY, NAME);
    this.config = config;
  }

  @Override
  public String[] getFileSuffixes() {
        Optional<PropertyDefinition> optionalPropertyDefinition = FooLanguageProperties.getProperties().stream().filter(it -> it.key().equals(FooLanguageProperties.FILE_SUFFIXES_KEY)).findFirst();
        String defaultSuffix = optionalPropertyDefinition.map(PropertyDefinition::defaultValue).orElse(null);
        String[] defaultSuffixes = {defaultSuffix};
        String[] configSuffixes = config.getStringArray(FooLanguageProperties.FILE_SUFFIXES_KEY);
        if (nullOrEmpty(defaultSuffixes)) {
            // TODO i want to throw exception here since it's bad already we don't have default settings of active language
            return new String[]{};
        }
        if (nullOrEmpty(configSuffixes))
            configSuffixes = defaultSuffixes;

        return configSuffixes;
     }

    <T> boolean nullOrEmpty(T[] ar) {
        return ar == null || ar.length == 0;
    }
}
