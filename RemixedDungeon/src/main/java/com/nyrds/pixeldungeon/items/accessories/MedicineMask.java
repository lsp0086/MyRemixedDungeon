package com.nyrds.pixeldungeon.items.accessories;

import com.nyrds.util.Util;
import com.watabou.pixeldungeon.Badges;

public class MedicineMask extends Accessory {
    {
        image = 19;
        coverFacialHair = true;
    }

    @Override
    public boolean nonIap() {
        return true;
    }

    @Override
    public boolean haveIt() {
        if (Util.isDebug()){
            return true;
        }
        return Badges.isUnlocked(Badges.Badge.DOCTOR_QUEST_COMPLETED);
    }
}
