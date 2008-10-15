/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.gen2.logging.impl.server;

import com.google.gwt.gen2.logging.impl.shared.LogImplWithManager;
import com.google.gwt.gen2.logging.server.ext.ServerLoggingConfiguration;
import com.google.gwt.gen2.logging.shared.Log;

/**
 * Default logging system used for java logging.
 */
public class LogImplServer extends LogImplWithManager {
  public static ServerLoggingConfiguration config = new ServerLoggingConfiguration();

  @Override
  public void init() {
    initializeLevels();
    Log.addLogHandler(config.getDefaultLogHandler());
  }

}