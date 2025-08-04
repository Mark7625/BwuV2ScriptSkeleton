package botwithus.areas;

import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Area;

public class GameAreas {
    public static final Coordinate BURTHORPE_BANK_LOCATION = new Coordinate(2889, 3537, 0);
    public static final Area BURTHORPE_BANK_AREA = new Area.Circular(BURTHORPE_BANK_LOCATION, 8);

    public static final Area CHICKEN_AREA = new Area.Rectangular(new Coordinate(2881, 3478, 0), new Coordinate(2889, 3475, 0));

    private GameAreas() {
    }
}
