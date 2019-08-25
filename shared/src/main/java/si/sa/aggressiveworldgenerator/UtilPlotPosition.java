package si.sa.aggressiveworldgenerator;

final class UtilPlotPosition {

    private UtilPlotPosition() { }

    static long vectorToId(Vector2i vector) {
        long n = Math.max(Math.abs(vector.x), Math.abs(vector.y));
        return ((n * (n - 1)) << 2) + toSpiralCoord(vector.x, vector.y, n);
    }

    static Vector2i idToVector(long plotid) {
        long nya = plotid >> 2;
        double xy = Math.sqrt((double) nya);
        double y = Math.ceil(xy);
        double x = Math.floor(xy);
        long n = (long) Math.max(x, y);
        long onThisLayer = n * 8;
        long countBefore = (long) ((x + y) * 8);
        return fromSpiralCoord(n, onThisLayer - countBefore + plotid - 1);
    }

    private static Vector2i fromSpiralCoord(long n, long spiralCoord) {
        Vector2i vector = new Vector2i(n, 1);
        if (spiralCoord > 0) {
            if (vector.y + spiralCoord > n * 2) {
                vector.y = n * 2 - 2;
                spiralCoord -= n * 2 - 1;
            }
        }
        if (spiralCoord > 0) {
            vector.x += Math.max(0, -spiralCoord);
        }
        return vector;
    }

    private static long toSpiralCoord(double x, double y, long n) {
        long tx = (long) (x + n) + 1;
        long ty = (long) (y + n) + 1;
        long lim = 1 + (n << 1);
        long tz = 0;
        if (ty != 1 && tx == lim)
            return tz + ty - 1;
        else
            tz += lim - 1;
        if (tx != lim && ty == lim)
            return lim - tx + tz;
        else
            tz += lim - 1;
        if (tx == 1)
            return lim - ty + tz;
        else
            tz += lim - 1;
        return tx + tz - 1;
    }

}
