package botwithus.areas;

import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Area;

public class GameAreas {
    public static final Coordinate BURTHORPE_BANK_LOCATION = new Coordinate(2889, 3537, 0);
    public static final Area BURTHORPE_BANK_AREA = new Area.Circular(BURTHORPE_BANK_LOCATION, 8);

    public static final Area COW_AREA = new Area.Polygonal(
            new Coordinate(2882, 3492, 0),
            new Coordinate(2889, 3492, 0),
            new Coordinate(2889, 3482, 0),
            new Coordinate(2881, 3482, 0),
            new Coordinate(2881, 3491, 0)
    );

    private GameAreas() {
    }
}
