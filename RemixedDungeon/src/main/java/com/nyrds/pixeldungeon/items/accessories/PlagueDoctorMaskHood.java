package com.nyrds.pixeldungeon.items.accessories;


import com.nyrds.util.Util;
import com.watabou.pixeldungeon.Badges;

public class PlagueDoctorMaskHood extends Accessory{

    {
        coverFacialHair = true;
        coverHair = true;
        image = 24;
    }

    @Override
    public boolean nonIap() {
        return true;
    }

    public boolean haveIt() {
        if (Util.isDebug()){
            return true;
        }
        return Badges.isUnlocked(Badges.Badge.DOCTOR_QUEST_COMPLETED);
    }
}
