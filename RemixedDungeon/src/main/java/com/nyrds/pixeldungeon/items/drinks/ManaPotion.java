package com.nyrds.pixeldungeon.items.drinks;

import com.nyrds.pixeldungeon.mechanics.CommonActions;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.platform.audio.Sample;
import com.nyrds.platform.util.StringsManager;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.effects.SpellSprite;
import com.watabou.pixeldungeon.utils.GLog;

import org.jetbrains.annotations.NotNull;

public class ManaPotion extends Drink {

	public ManaPotion() {
		imageFile = "items/drinks.png";
		image = 0;
	}
	
	@Override
	public int price() {
		return 100 * quantity();
	}

	@Override
	public void _execute(@NotNull Char chr, @NotNull String action ) {
		if (action.equals(CommonActions.AC_DRINK)) {
			detach(chr.getBelongings().backpack);
			GLog.i(StringsManager.getVar(R.string.Drink_Message));

			chr.setSkillPoints(chr.getSkillPoints() + chr.getSkillPointsMax() / 3);
			chr.doOperate(TIME_TO_DRINK);

			SpellSprite.show(chr, SpellSprite.FOOD);
			Sample.INSTANCE.play(Assets.SND_DRINK);
		} else {
			super._execute(chr, action);
		}
	}

}
