package org.sonarsource.plugins.example.rules;

import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;

public class MyFooSensorTest {


  @Test
  public void getRangeTest(){
    MyFooSensor sensor = new MyFooSensor();
    InputFile file = new DefaultInputFile();
    sensor.getTextRange()
  }

}
