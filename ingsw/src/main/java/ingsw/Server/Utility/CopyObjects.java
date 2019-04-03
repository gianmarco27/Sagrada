package ingsw.Server.Utility;

import ingsw.Server.Grid.GridBag;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class CopyObjects {
    private static final Logger log = Logger.getLogger( CopyObjects.class.getName() );

    /**
     * This method makes a "deep clone" of any Java object it is given.
     */
    public static Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            log.warning(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
    public static String serializeToB64(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    public static Serializable deserializeFromB64(String s) throws IOException, ClassNotFoundException {
        byte[] decoded = Base64.getDecoder().decode(s);
        ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
        ObjectInputStream ois = new ObjectInputStream( bais );
        return (Serializable) ois.readObject();
        }

}
