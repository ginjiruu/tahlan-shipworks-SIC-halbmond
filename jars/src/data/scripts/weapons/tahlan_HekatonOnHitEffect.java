package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.scripts.util.MagicLensFlare;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class tahlan_HekatonOnHitEffect implements OnHitEffectPlugin {

    private static final float BONUS_DAMAGE = 200f;
    private static final Color COLOR1 = new Color(71, 191, 255);
    private static final Color COLOR2 = new Color(236, 248, 255);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, CombatEngineAPI engine) {
        Global.getCombatEngine().applyDamage(target, point, BONUS_DAMAGE, DamageType.ENERGY, BONUS_DAMAGE, false, false, null, true);
        MagicLensFlare.createSharpFlare(engine, projectile.getSource(), projectile.getLocation(), 10, 600, 0, new Color(186, 240, 255), new Color(255, 255, 255));

        if (point != null) {

            if (target instanceof ShipAPI) {
                ShipAPI ship = (ShipAPI) target;

                float hitLevel = 0f;
                int numHits = MathUtils.getRandomNumberInRange(0,3);
                for (int x = 0; x < numHits; x++) {
                    float pierceChance = ((ShipAPI) target).getHardFluxLevel() - 0.5f;
                    pierceChance *= ship.getMutableStats().getDynamic().getValue(Stats.SHIELD_PIERCED_MULT);

                    boolean piercedShield = shieldHit && (float) Math.random() < pierceChance;
                    if (!shieldHit || piercedShield) {
                        hitLevel += 0.25f;
                        engine.spawnEmpArcPierceShields(projectile.getSource(), point, ship, ship,
                                DamageType.ENERGY, BONUS_DAMAGE, BONUS_DAMAGE, 100000f, null, 20f, COLOR1, COLOR2);
                    }
                }

                if (hitLevel > 0f) {
                    engine.addSmoothParticle(point, new Vector2f(0f, 0f), 300f * hitLevel, hitLevel, 0.75f, COLOR1);
                    Global.getSoundPlayer().playSound("tachyon_lance_emp_impact", 1f, 1f * hitLevel, point, new Vector2f(0f, 0f));
                }
            }
        }
    }
}