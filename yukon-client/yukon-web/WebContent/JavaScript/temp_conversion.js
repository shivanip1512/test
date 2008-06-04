function getConvertedTemp(degreesFahrenheit, unit) {
  if (unit == 'F') {
    return degreesFahrenheit;
  } else {
    // convert to celsius
    return Math.round((degreesFahrenheit - 32) / 9 * 5);
  }
}

function getFahrenheitTemp(degreesEither, unit) {
  if (unit == 'F') {
    return degreesEither;
  } else {
    // convert to fahrenheit
    var degreesCelsius = degreesEither;
    return Math.round((degreesCelsius / 5 * 9) + 32);
  }
}

function getRawConvertedTemp(degreesFahrenheit, unit) {
  if (unit == 'F') {
    return degreesFahrenheit;
  } else {
    // convert to celsius
    return (degreesFahrenheit - 32) / 9 * 5;
  }
}

function getRawFahrenheitTemp(degreesEither, unit) {
  if (unit == 'F') {
    return degreesEither;
  } else {
    // convert to fahrenheit
    var degreesCelsius = degreesEither;
    return (degreesCelsius / 5 * 9) + 32;
  }
}

