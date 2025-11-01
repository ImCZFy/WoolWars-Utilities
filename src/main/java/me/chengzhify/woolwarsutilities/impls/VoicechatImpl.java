package me.chengzhify.woolwarsutilities.impls;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.LeaveGroupEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;

import java.util.UUID;

import static org.apache.logging.log4j.LogManager.getLogger;

public class VoicechatImpl implements VoicechatPlugin {
    public static VoicechatServerApi voiceServerApi;
    @Override
    public String getPluginId() {
        return WoolWarsUtilities.PLUGIN_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {

    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onVoiceServerStart);
        registration.registerEvent(LeaveGroupEvent.class, this::onLeaveGroup);
    }

    private void onVoiceServerStart(VoicechatServerStartedEvent event) {
        voiceServerApi = event.getVoicechat();
        System.out.println(voiceServerApi);
        getLogger().info("[VoiceChat] API 已获取，可以创建语音频道了！");
    }

    private void onLeaveGroup(LeaveGroupEvent event) {
    }

    public static UUID createGroup(String name, String team) {
        Group g = voiceServerApi.groupBuilder()
                .setPersistent(true)
                .setName(name + team) // The name of the group
                .setPassword("IWontTellYouThePasswordUnlessYouFindItOnYouOwn")
                .setType(Group.Type.ISOLATED)
                .build();
        getLogger().info("[WoolWars - VoiceGroup] 竞技场语音队伍已创建");
        return g.getId();
    }

}