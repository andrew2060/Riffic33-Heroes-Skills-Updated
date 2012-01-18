package dev.riffic33.heroes.skills;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.SkillResult;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.SkillConfigManager;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;


public class SkillLifeTap extends ActiveSkill {
	
    public SkillLifeTap(Heroes plugin) {
        super(plugin, "Lifetap");
        setDescription("Convert $1% health to $2% mana.");
        setUsage("/skill lifetap");
        setArgumentRange(0, 0);
        setIdentifiers("skill lifetap");
        setTypes(SkillType.SILENCABLE);  
    }
   
    @Override
    public ConfigurationSection getDefaultConfig() {
        ConfigurationSection node = super.getDefaultConfig();
        node.set("HealthLossPercentage", 10);
        node.set("Manaregain", 10);
        node.set("Threshold", 20);
        return  node;
    }
    
    @Override
    public SkillResult use(Hero hero, String[] args) {
    	Player player = hero.getPlayer();
    	
    	int pctLoss = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "HealthLossPercentage", 10);
    	int manaGain = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "Manaregain", 10);
    	int thresHold = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "Threshold", 20);
    	
    	double max = hero.getMaxHealth();
        if(hero.getHealth() < max*((double)thresHold/100D) || hero.getMana() >= 100){
        	if(hero.getMana() >= 100){
        		Messaging.send(player, "Mana is already full");
        	}else{
        		Messaging.send(player, "Health is too low to use life tap");
        	}	
        	return SkillResult.CANCELLED;
        }else{
        	int damage = (int) (Math.floor(max*((double)pctLoss/100D)));
        	player.damage(damage, player);
        	hero.setMana( hero.getMana() + manaGain > 100 ? 100 : hero.getMana()+manaGain ); 
        	return SkillResult.NORMAL;
        }
    }
    @Override
    public String getDescription(Hero hero) {
    	int healthLoss = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "HealthLossPercentage", 10);
    	int manaGain = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "Manaregain", 10);
    	int thresHold = (int) SkillConfigManager.getSetting(hero.getHeroClass(), this, "Threshold", 20);
    	
        return thresHold <= 0 ? getDescription().replace("$1", healthLoss + "").replace("$2", manaGain + "") : getDescription().replace("$1", healthLoss + "").replace("$2", manaGain + "").concat(" Not available with less than " +thresHold+ "% health left");
    }
    
}