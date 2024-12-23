
package com.watabou.pixeldungeon.actors.buffs;

import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.util.StringsManager;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance;
import com.watabou.pixeldungeon.sprites.CharSprite;
import com.watabou.pixeldungeon.ui.BuffIndicator;

public class Vertigo extends FlavourBuff {
	
	public static final float DURATION	= 10f;
	
	@Override
	public int icon() {
		return BuffIndicator.VERTIGO;
	}
	
	public static float duration( Char ch ) {
		Resistance r = ch.buff( Resistance.class );
		return r != null ? r.durationFactor() * DURATION : DURATION;
	}

	@Override
	public void attachVisual() {
        target.showStatus(CharSprite.NEGATIVE, StringsManager.getVar(R.string.Char_StaDizzy));
	}
}
