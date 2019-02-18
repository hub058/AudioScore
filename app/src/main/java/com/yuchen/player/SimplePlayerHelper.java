package com.yuchen.player;

public class SimplePlayerHelper {
    public static final int TYPE_MEDIA_PLAYER = 0x0000;
    public static final int TYPE_AUDIO_TRACK = 0x000f;

    public static ISimplePlayer getPlayer(int type) {
        switch (type) {
            case TYPE_MEDIA_PLAYER:
                return MySimplePlayer.getInstance();
            case TYPE_AUDIO_TRACK:
                return MyAudioTrack.getInstance();
        }
        return null;
    }
}
