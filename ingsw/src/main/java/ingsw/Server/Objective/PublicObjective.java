package ingsw.Server.Objective;

public abstract class PublicObjective extends GenericObjective {

    int type = 1;

    @Override
    public String toString() {
        return "Public" + super.toString() + " | SCORE " + getObjValue();
    }
}

