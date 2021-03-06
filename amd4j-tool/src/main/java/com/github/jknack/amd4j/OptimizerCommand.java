/**
 * Copyright (c) 2013 Edgar Espina
 *
 * This file is part of amd4j (https://github.com/jknack/amd4j)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jknack.amd4j;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.Validate.isTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Run the optimize command.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
@Parameters(commandNames = "-o", separators = "=")
public class OptimizerCommand extends BaseCommand {

  /**
   * The output's file.
   */
  @Parameter(names = "-out", description = "Output file")
  private File out;

  /**
   * Inline text in the final output. Default: true.
   */
  @Parameter(names = "-inlineText",
      description = "Inlines the text for any text! dependencies, to avoid the separate "
          + "async XMLHttpRequest calls to load those dependencies. Default: true", arity = 1)
  private Boolean inlineText;

  /**
   * Remove "useStrict"; statement from output.
   */
  @Parameter(names = "-useStrict", description = "Allow \"use strict\"; be included in the "
      + "JavaScript files. Default: false", arity = 1)
  private Boolean useStrict;

  /**
   * An optional build profile.
   */
  @Parameter(description = "[build.js]")
  private List<String> buildFile = new ArrayList<String>();

  @Override
  public void doExecute(final Amd4j amd4j, final Config config) throws IOException {
    isTrue(config.getOut() != null, "The following option is required: %s", "out");
    isTrue(!isEmpty(config.getBaseUrl()), "The following option is required: %s", "baseUrl");

    System.out.printf("optimizing %s...\n", config.getName());
    long start = System.currentTimeMillis();
    Module module = amd4j.optimize(config);
    long end = System.currentTimeMillis();
    System.out.printf("%s\n", module.toStringTree().trim());
    System.out.printf("optimization of %s took %sms\n\n", out.getPath(), end - start,
        out.getAbsolutePath());
  }

  @Override
  protected Config newConfig() throws IOException {
    if (buildFile.size() == 1) {
      return Config.parse(new File(buildFile.get(0)));
    } else {
      return super.newConfig();
    }
  }

  @Override
  protected Config merge(final Config config) {
    super.merge(config);
    if (out != null) {
      config.setOut(out);
    }
    if (inlineText != null) {
      config.setInlineText(inlineText.booleanValue());
    }
    if (useStrict != null) {
      config.setUseStrict(useStrict.booleanValue());
    }
    return config;
  }
}
