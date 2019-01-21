package com.cannontech.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.system.ThirdPartyIconLibrary;
import com.cannontech.system.ThirdPartyLibraries;
import com.cannontech.system.ThirdPartyLibraryParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ThirdPartyIconLibraryTest {

    private static String iconBasePath = "WebContent/WebConfig/yukon/Icons";
    private static Set<Path> ignoredIcons = buildIgnoredIconSet();
    
    private static Stream<Path> recurse(Path p) {
        if (Files.isDirectory(p)) {
            try {
                return Files.list(p)
                        .flatMap(ThirdPartyIconLibraryTest::recurse);
            } catch (IOException e) {
                throw new RuntimeException("Could not list files in " + p, e);
            }
        }
        return Stream.of(p);
    }
    
    @Test
    public void test_thirdPartyLicenses() throws IOException, NoSuchAlgorithmException {

        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        
        ThirdPartyLibraries documentedLibraries = ThirdPartyLibraryParser.parse(libraryYaml.getInputStream());
        
        Map<Path, ThirdPartyIconLibrary> documentedLibrariesByProject = 
                Maps.uniqueIndex(documentedLibraries.iconLibraries, 
                                 l -> Paths.get(iconBasePath, l.path)); 
        
        Set<Path> allIcons = Files.list(Paths.get(iconBasePath))
                .flatMap(ThirdPartyIconLibraryTest::recurse)
                .collect(Collectors.toSet());

        Set<Path> thirdPartyIcons = Sets.filter(allIcons, p -> !ignoredIcons.contains(p));
        
        Set<Path> unknownFiles = Sets.difference(thirdPartyIcons, documentedLibrariesByProject.keySet());
        assertTrue("Unknown icons found: " + unknownFiles, unknownFiles.isEmpty());

        Set<Path> missingFiles = Sets.difference(documentedLibrariesByProject.keySet(), thirdPartyIcons);
        assertTrue("Icons listed in thirdPartyLibraries.yaml, but missing from WebContent/WebConfig/yukon/Icons: " + missingFiles, missingFiles.isEmpty());
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByProject.entrySet().stream().forEach(e -> {
            assertFalse(e.getKey() + " must have a project name", StringUtils.isEmpty(e.getValue().project));
            assertFalse(e.getKey() + " must have a project version", StringUtils.isEmpty(e.getValue().version));
            assertFalse(e.getKey() + " must have a project URL", StringUtils.isEmpty(e.getValue().projectUrl));
            assertFalse(e.getKey() + " must have a license type", CollectionUtils.isEmpty(e.getValue().licenses));
            assertFalse(e.getKey() + " must have a license URL", CollectionUtils.isEmpty(e.getValue().licenseUrls));
            assertFalse(e.getKey() + " must have a JIRA entry", StringUtils.isEmpty(e.getValue().jira));
            assertNotNull(e.getKey() + " must have an updated date", e.getValue().updated);
            
            md_md5.reset();
            md_sha1.reset();
            
            byte[] contents;
            try {
                contents = Files.readAllBytes(e.getKey());
            } catch (IOException e1) {
                throw new RuntimeException("Could not read " + e.getKey(), e1);
            }
            String md5 = Hex.encodeHexString(md_md5.digest(contents));
            String sha1 = Hex.encodeHexString(md_sha1.digest(contents));

            assertEquals("MD5 mismatch for " + e.getKey(), e.getValue().md5, md5);
            assertEquals("SHA1 mismatch for " + e.getKey(), e.getValue().sha1, sha1);
        });
    }
    
    private static Set<Path> buildIgnoredIconSet() {
        return Lists.newArrayList(
                    "$$$.gif", 
                    "$$$Sm.gif", 
                    "$$.gif", 
                    "$$Sm.gif", 
                    "$.gif", 
                    "$Sm.gif", 
                    "128/abacus.png", 
                    "128/access_point.png", 
                    "128/add.png", 
                    "128/administrator.png", 
                    "128/alarm.png", 
                    "128/arrow_bidirectional.png", 
                    "128/arrow_down2.png", 
                    "128/arrow_left2.png", 
                    "128/arrow_right2.png", 
                    "128/arrow_up2.png", 
                    "128/attachment.png", 
                    "128/audio_knob.png", 
                    "128/barcode.png", 
                    "128/battery_empty.png", 
                    "128/battery_full.png", 
                    "128/battery_half.png", 
                    "128/bell.png", 
                    "128/bill.png", 
                    "128/binoculars.png", 
                    "128/bold.png", 
                    "128/book.png", 
                    "128/bookmark.png", 
                    "128/briefcase.png", 
                    "128/brightness.png", 
                    "128/broken_link.png", 
                    "128/brush.png", 
                    "128/burn_blu-ray.png", 
                    "128/burn_blu-ray2.png", 
                    "128/burn_dvd.png", 
                    "128/burn_dvd2.png", 
                    "128/cabinet.png", 
                    "128/calculator.png", 
                    "128/calendar.png", 
                    "128/camera.png", 
                    "128/cancel.png", 
                    "128/card_clubs.png", 
                    "128/card_diamonds.png", 
                    "128/card_hearts.png", 
                    "128/card_spades.png", 
                    "128/certificate-(2).png", 
                    "128/certificate.png", 
                    "128/chat-exclamation.png", 
                    "128/checkmark.png", 
                    "128/checkmark2.png", 
                    "128/clip.png", 
                    "128/clipboard.png", 
                    "128/clock.png", 
                    "128/close.png", 
                    "128/cloud.png", 
                    "128/cloud2.png", 
                    "128/coin.png", 
                    "128/compress.png", 
                    "128/connect.png", 
                    "128/contrast.png", 
                    "128/copy.png", 
                    "128/cross.png", 
                    "128/cutter.png", 
                    "128/delete.png", 
                    "128/dial.png", 
                    "128/diary.png", 
                    "128/dimensions.png", 
                    "128/directional_down.png", 
                    "128/directional_left.png", 
                    "128/directional_right.png", 
                    "128/directional_up.png", 
                    "128/disconnect.png", 
                    "128/diskette.png", 
                    "128/document.png", 
                    "128/door.png", 
                    "128/download2.png", 
                    "128/dropper.png", 
                    "128/earphones.png", 
                    "128/effects.png", 
                    "128/eject.png", 
                    "128/emoticon_angry.png", 
                    "128/emoticon_confused.png", 
                    "128/emoticon_grin.png", 
                    "128/emoticon_in_love.png", 
                    "128/emoticon_sad.png", 
                    "128/emoticon_sleeping.png", 
                    "128/emoticon_smile.png", 
                    "128/encrypt.png", 
                    "128/eraser.png", 
                    "128/eye.png", 
                    "128/eye_closed.png", 
                    "128/fast_forward.png", 
                    "128/file.png", 
                    "128/fill.png", 
                    "128/fingerprint.png", 
                    "128/firewall.png", 
                    "128/first.png", 
                    "128/folder.png",
                    "128/font.png", 
                    "128/font_size.png", 
                    "128/game_control.png", 
                    "128/gear.png", 
                    "128/group.png", 
                    "128/hammer.png", 
                    "128/hand_point.png", 
                    "128/hand_thumbsdown.png", 
                    "128/hand_thumbsup.png", 
                    "128/hard_disk.png", 
                    "128/headset.png", 
                    "128/heart.png", 
                    "128/help.png", 
                    "128/help2.png", 
                    "128/history.png", 
                    "128/home.png", 
                    "128/hourglass.png", 
                    "128/hourglass2.png", 
                    "128/id.png", 
                    "128/info.png", 
                    "128/info2.png", 
                    "128/italic.png", 
                    "128/item.png", 
                    "128/key.png", 
                    "128/last.png", 
                    "128/lightbulb.png", 
                    "128/link.png", 
                    "128/list.png", 
                    "128/location.png", 
                    "128/lock.png", 
                    "128/lock_open.png", 
                    "128/login.png", 
                    "128/mail.png", 
                    "128/mail_open.png", 
                    "128/messenger.png", 
                    "128/meter1.png", 
                    "128/meter2.png", 
                    "128/meter3.png", 
                    "128/microhpone.png", 
                    "128/microphone.png", 
                    "128/money_bag.png", 
                    "128/monitor.png", 
                    "128/moon.png", 
                    "128/music.png", 
                    "128/music_folder.png", 
                    "128/music_library.png", 
                    "128/next.png", 
                    "128/notepad.png", 
                    "128/openadr.png", 
                    "128/paragraph_align_left.png", 
                    "128/paragraph_align_right.png", 
                    "128/paragraph_justify.png", 
                    "128/password.png", 
                    "128/paste.png", 
                    "128/pause.png", 
                    "128/pen.png", 
                    "128/pencil.png", 
                    "128/phone.png", 
                    "128/photo_album.png", 
                    "128/pictures_folder.png", 
                    "128/play.png", 
                    "128/point.png", 
                    "128/power.png", 
                    "128/previous.png", 
                    "128/print.png", 
                    "128/pyramid.png", 
                    "128/random.png", 
                    "128/record.png", 
                    "128/redo.png", 
                    "128/reload.png", 
                    "128/repeat.png", 
                    "128/resize2.png", 
                    "128/rewind.png", 
                    "128/rotate.png", 
                    "128/round.png", 
                    "128/ruler_square.png", 
                    "128/satellite.png", 
                    "128/scissors.png", 
                    "128/screwdriver.png", 
                    "128/security.png", 
                    "128/shopping_basket.png", 
                    "128/software24.png", 
                    "128/spam.png", 
                    "128/speaker.png", 
                    "128/speaker2.png", 
                    "128/sphere.png", 
                    "128/spreadsheet.png", 
                    "128/square.png", 
                    "128/star.png", 
                    "128/stats_bars.png", 
                    "128/stats_lines.png", 
                    "128/stats_pie.png", 
                    "128/stop.png", 
                    "128/strike_through.png", 
                    "128/sun.png", 
                    "128/target.png", 
                    "128/thunder.png", 
                    "128/trash_can.png", 
                    "128/underlined.png", 
                    "128/undo.png", 
                    "128/upload2.png", 
                    "128/usb.png", 
                    "128/user.png", 
                    "128/user_woman.png", 
                    "128/volume_control.png", 
                    "128/webcam.png", 
                    "128/window.png", 
                    "128/wizard.png", 
                    "128/world.png", 
                    "128/zoom.png", 
                    "128/zoom_in.png", 
                    "128/zoom_out.png", 
                    "32/YukonIcon32.png", 
                    "64/access_point.png", 
                    "64/arrow_bidirectional.png", 
                    "64/calculator.png", 
                    "64/connect.png", 
                    "64/document.png", 
                    "64/file.png", 
                    "64/firewall.png", 
                    "64/folder.png", 
                    "64/gear.png", 
                    "64/home.png", 
                    "64/item.png", 
                    "64/lightbulb.png", 
                    "64/list.png", 
                    "64/lock.png", 
                    "64/lock_open.png", 
                    "64/meter1.png", 
                    "64/money_bag.png", 
                    "64/monitor.png", 
                    "64/openadr.png", 
                    "64/phone.png", 
                    "64/random.png", 
                    "64/security.png", 
                    "64/stats_bars.png", 
                    "64/stats_lines.png", 
                    "64/stats_pie.png", 
                    "64/sun.png", 
                    "64/window.png", 
                    "64/world.png", 
                    "AC.png", 
                    "ACSm.gif", 
                    "DualFuel.png", 
                    "DualFuelSm.gif", 
                    "Electric.png", 
                    "ElectricSm.gif", 
                    "Generation.png", 
                    "GenerationSm.gif", 
                    "GrainDryer.png", 
                    "GrainDryerSm.gif", 
                    "Half.gif", 
                    "HalfSm.gif", 
                    "HeatPump.png", 
                    "HeatPumpSm.gif", 
                    "HotTub.png", 
                    "HotTubSm.gif", 
                    "Irrigation.png", 
                    "IrrigationSm.gif", 
                    "Load.png", 
                    "Pool.png", 
                    "PoolPumpSm.gif", 
                    "Quarter.gif", 
                    "QuarterSm.gif", 
                    "Setback.png", 
                    "SetbackSm.gif", 
                    "Sixth.gif", 
                    "SixthSm.gif", 
                    "StartCalendar.png", 
                    "StorageHeat.png", 
                    "StorageHeatSm.gif", 
                    "Third.gif", 
                    "ThirdSm.gif", 
                    "Tree1Sm.gif", 
                    "Tree2Sm.gif", 
                    "Tree3Sm.gif", 
                    "TwoThirds.gif", 
                    "VerticalRule.gif", 
                    "WaterHeater.png", 
                    "WaterSm.gif", 
                    "Whole.gif", 
                    "accept.png", 
                    "add.png", 
                    "arrow_down_green_anim.gif", 
                    "arrow_down_orange_anim.gif", 
                    "arrow_down_red.gif", 
                    "arrow_fastforward.gif", 
                    "arrow_first.gif", 
                    "arrow_last.gif", 
                    "arrow_rewind.gif", 
                    "arrow_trend_down_right.gif", 
                    "arrow_trend_up_right.gif", 
                    "arrow_up_green_anim.gif", 
                    "arrow_up_orange_anim.gif", 
                    "arrow_up_red.gif", 
                    "bullet_arrow_down.png", 
                    "busy_rotation.gif", 
                    "dashboardImages.png", 
                    "delete.png", 
                    "electric_128.png", 
                    "electric_16.png", 
                    "electric_256.png", 
                    "electric_32.png", 
                    "electric_512.png", 
                    "electric_64.png", 
                    "error.gif", 
                    "gas_128.png", 
                    "gas_16.png", 
                    "gas_256.png", 
                    "gas_32.png", 
                    "gas_512.png", 
                    "gas_64.png", 
                    "green_local.png", 
                    "icon_blockcollapsed.png", 
                    "icon_blockexpanded.png", 
                    "icons-32-disabled.png", 
                    "icons-32.png", 
                    "icons-app-32-disabled.png", 
                    "icons-app-32.png", 
                    "icons-disabled.png", 
                    "information.gif", 
                    "marker-blank.png", 
                    "marker-generic.png", 
                    "marker-lcr-grey.png", 
                    "marker-lcr.png", 
                    "marker-meter-elec-grey.png", 
                    "marker-meter-elec.png", 
                    "marker-meter-gas-grey.png", 
                    "marker-meter-gas.png", 
                    "marker-meter-plc-elec-grey.png", 
                    "marker-meter-water-grey.png", 
                    "marker-meter-water.png", 
                    "marker-plc-lcr-grey.png", 
                    "marker-relay-grey.png", 
                    "marker-thermostat-grey.png", 
                    "marker-thermostat.png", 
                    "marker-transmitter-grey.png", 
                    "marker-transmitter.png", 
                    "marker.png", 
                    "plus-minus.png", 
                    "relay.png", 
                    "resultset_next.gif", 
                    "resultset_previous.gif", 
                    "slider-handle.png", 
                    "spinner-white.gif", 
                    "spinner.gif", 
                    "time.gif", 
                    "triangle-down_white.gif", 
                    "triangle-right.gif", 
                    "triangle-right_white.gif", 
                    "warning.gif", 
                    "water_128.png", 
                    "water_16.png", 
                    "water_256.png", 
                    "water_32.png", 
                    "water_512.png", 
                    "water_64.png")
                .stream()
                .map(i -> Paths.get(iconBasePath, i))
                .collect(Collectors.toSet());
    }
}
