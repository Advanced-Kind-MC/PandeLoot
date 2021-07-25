package net.seyarada.pandeloot.options;

import net.seyarada.pandeloot.options.conditions.ChanceCondition;
import net.seyarada.pandeloot.options.conditions.DamageCondition;
import net.seyarada.pandeloot.options.conditions.LastHitCondition;
import net.seyarada.pandeloot.options.conditions.PermissionBlacklistCondition;
import net.seyarada.pandeloot.options.conditions.PermissionCondition;
import net.seyarada.pandeloot.options.conditions.TopCondition;
import net.seyarada.pandeloot.options.mechanics.ActionbarMechanic;
import net.seyarada.pandeloot.options.mechanics.BroadcastMechanic;
import net.seyarada.pandeloot.options.mechanics.CommandMechanic;
import net.seyarada.pandeloot.options.mechanics.DiscordMechanic;
import net.seyarada.pandeloot.options.mechanics.ExperienceMechanic;
import net.seyarada.pandeloot.options.mechanics.ExplodeMechanic;
import net.seyarada.pandeloot.options.mechanics.GlowMechanic;
import net.seyarada.pandeloot.options.mechanics.HolobroadcastMechanic;
import net.seyarada.pandeloot.options.mechanics.HologramMechanic;
import net.seyarada.pandeloot.options.mechanics.MMOCoreExperienceMechanic;
import net.seyarada.pandeloot.options.mechanics.MessageMechanic;
import net.seyarada.pandeloot.options.mechanics.MoneyMechanic;
import net.seyarada.pandeloot.options.mechanics.RemoveMechanic;
import net.seyarada.pandeloot.options.mechanics.SoundMechanic;
import net.seyarada.pandeloot.options.mechanics.TitleMechanic;
import net.seyarada.pandeloot.options.mechanics.ToastMechanic;
import net.seyarada.pandeloot.options.mechanics.TotemMechanic;
import net.seyarada.pandeloot.options.mechanics.VisibilityMechanic;

public class RegisterOptions {
  public RegisterOptions() {
    registerConditions();
    registerMechanics();
  }
  
  private void registerConditions() {
    Conditions.registerOption((ConditionEvent)new ChanceCondition());
    Conditions.registerOption((ConditionEvent)new DamageCondition());
    Conditions.registerOption((ConditionEvent)new LastHitCondition());
    Conditions.registerOption((ConditionEvent)new PermissionBlacklistCondition(), new String[] { "permissionblacklist", "pbl" });
    Conditions.registerOption((ConditionEvent)new PermissionCondition(), new String[] { "permission" });
    Conditions.registerOption((ConditionEvent)new TopCondition(), new String[] { "top" });
  }
  
  private void registerMechanics() {
    Options.registerOption((MechanicEvent)new ActionbarMechanic(), OptionType.PLAYER, new String[] { "actionbar" });
    Options.registerOption((MechanicEvent)new BroadcastMechanic(), OptionType.GENERAL, new String[] { "broadcast" });
    Options.registerOption((MechanicEvent)new CommandMechanic(), OptionType.GENERAL, new String[] { "command", "cmd" });
    Options.registerOption((MechanicEvent)new DiscordMechanic(), OptionType.GENERAL, new String[] { "dchannel" });
    Options.registerOption((MechanicEvent)new ExperienceMechanic(), OptionType.PLAYER, new String[] { "experience", "exp", "xp" });
    Options.registerOption((MechanicEvent)new ExplodeMechanic(), OptionType.ITEM, new String[] { "explode" });
    Options.registerOption((MechanicEvent)new GlowMechanic(), OptionType.ITEM, new String[] { "glow" });
    Options.registerOption((MechanicEvent)new HolobroadcastMechanic(), OptionType.PLAYER, new String[] { "holobroadcast", "hb" });
    Options.registerOption((MechanicEvent)new HologramMechanic(), OptionType.ITEM, new String[] { "hologram" });
    Options.registerOption((MechanicEvent)new MessageMechanic(), OptionType.PLAYER, new String[] { "message", "msg" });
    Options.registerOption((MechanicEvent)new MMOCoreExperienceMechanic(), OptionType.PLAYER, new String[] { "mmocexp", "coreexp" });
    Options.registerOption((MechanicEvent)new MoneyMechanic(), OptionType.PLAYER, new String[] { "money", "eco" });
    Options.registerOption((MechanicEvent)new RemoveMechanic(), OptionType.GENERAL, new String[] { "remove" });
    Options.registerOption((MechanicEvent)new SoundMechanic(), OptionType.PLAYER, new String[] { "sound" });
    Options.registerOption((MechanicEvent)new TitleMechanic(), OptionType.PLAYER, new String[] { "title", "subtitle" });
    Options.registerOption((MechanicEvent)new ToastMechanic(), OptionType.PLAYER, new String[] { "toast", "toasttitle" });
    Options.registerOption((MechanicEvent)new TotemMechanic(), OptionType.PLAYER, new String[] { "totemeffect", "playtotem", "totem" });
    Options.registerOption((MechanicEvent)new VisibilityMechanic(), OptionType.ITEM, new String[] { "visibility", "canview" });
  }
}
