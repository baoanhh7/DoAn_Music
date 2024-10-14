package com.example.doan_music.Lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LRCParser {
    private static final Pattern TIME_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]");

    public static List<LyricsSyncManager.LyricLine> parse(InputStream inputStream) throws IOException {
        List<LyricsSyncManager.LyricLine> lyrics = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = reader.readLine()) != null) {
            Matcher matcher = TIME_PATTERN.matcher(line);
            while (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int hundredths = Integer.parseInt(matcher.group(3));
                long timeMs = (minutes * 60 * 1000) + (seconds * 1000) + (hundredths * 10);

                String text = line.substring(matcher.end()).trim();
                if (!text.isEmpty()) {
                    lyrics.add(new LyricsSyncManager.LyricLine(timeMs, text));
                }
            }
        }

        return lyrics;
    }
}
