package labca1.common;

public record OccupabilityRecord(
    String cutDate,
    int year,
    int month,
    String classId,
    String className,
    String categoryId,
    String categoryName,
    String department,
    String ubigeo,
    long establishments,
    long rooms,
    long beds,
    double tnoh,
    double tnoc,
    double averageStay,
    double nationalStay,
    double foreignStay,
    long arrivals,
    long nationalArrivals,
    long foreignArrivals,
    long overnightStays,
    long nationalOvernightStays,
    long foreignOvernightStays,
    long employment) {

  private static final int COLUMN_COUNT = 24;

  public static OccupabilityRecord parse(String line) {
    if (line.startsWith("FECHA_CORTE;ANIO;")) {
      throw new IllegalArgumentException("no se puede parsear la cabecera");
    }

    String[] columns = line.split(";", -1);
    if (columns.length != COLUMN_COUNT) {
      throw new IllegalArgumentException(
          "se esperaban 24 columnas y se recibieron " + columns.length);
    }

    try {
      return new OccupabilityRecord(
          columns[0],
          Integer.parseInt(columns[1]),
          Integer.parseInt(columns[2]),
          columns[3],
          columns[4],
          columns[5],
          columns[6],
          columns[7],
          columns[8],
          Long.parseLong(columns[9]),
          Long.parseLong(columns[10]),
          Long.parseLong(columns[11]),
          Double.parseDouble(columns[12]),
          Double.parseDouble(columns[13]),
          Double.parseDouble(columns[14]),
          Double.parseDouble(columns[15]),
          Double.parseDouble(columns[16]),
          Long.parseLong(columns[17]),
          Long.parseLong(columns[18]),
          Long.parseLong(columns[19]),
          Long.parseLong(columns[20]),
          Long.parseLong(columns[21]),
          Long.parseLong(columns[22]),
          Long.parseLong(columns[23]));
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("registro con valor numérico inválido", exception);
    }
  }

  public boolean isOverall() {
    return "TT".equals(classId) && "TT".equals(categoryId);
  }

  public boolean isClassTotal() {
    return "TT".equals(categoryId);
  }
}
