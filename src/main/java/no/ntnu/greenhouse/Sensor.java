package no.ntnu.greenhouse;

/**
 * A sensor which can sense the environment in a specific way.
 */
public class Sensor {
  private final SensorReading reading;
  private final double min;
  private final double max;

  /**
   * Create a sensor.
   *
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public Sensor(String type, double min, double max, double current, String unit) {
    this.reading = new SensorReading(type, current, unit);
    this.min = min;
    this.max = max;
    ensureValueBoundsAndPrecision(current);
  }

  /**
   * Get the type of the sensor.
   *
   * @return The type of the sensor
   */
  public String getType() {
    return reading.getType();
  }

  /**
   * Get the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public SensorReading getReading() {
    return reading;
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public Sensor createClone() {
    return new Sensor(this.reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
  }

  /**
   * Add a random noise to the sensors to simulate realistic values.
   */
  public void addRandomNoise() {
    double newValue = this.reading.getValue() + generateRealisticNoise();
    ensureValueBoundsAndPrecision(newValue);
  }

  /**
   * Ensure that the value is within the allowed bounds and has the correct precision.
   *
   * @param newValue The new value to ensure the bounds and precision for
   */
  private void ensureValueBoundsAndPrecision(double newValue) {
    newValue = roundToTwoDecimals(newValue);
    if (newValue < min) {
      newValue = min;
    } else if (newValue > max) {
      newValue = max;
    }
    reading.setValue(newValue);
  }

  /**
   * Round a value to two decimals.
   *
   * @param value The value to round
   * @return The value rounded to two decimals
   */
  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  /**
   * Generate a realistic noise for the sensor.
   *
   * @return A realistic noise value
   */
  private double generateRealisticNoise() {
    final double wholeRange = max - min;
    final double onePercentOfRange = wholeRange / 100.0;
    final double zeroToTwoPercent = Math.random() * onePercentOfRange * 2;
    return zeroToTwoPercent - onePercentOfRange; // In the range [-1%..+1%]
  }

  /**
   * Apply an external impact (from an actuator) to the current value of the sensor.
   *
   * @param impact The impact to apply - the delta for the value
   */
  public void applyImpact(double impact) {
    double newValue = this.reading.getValue() + impact;
    ensureValueBoundsAndPrecision(newValue);
  }

  /**
   * Get the string representation of the sensor.
   *
   * @return The string representation of the sensor
   */
  @Override
  public String toString() {
    return reading.toString();
  }
}
