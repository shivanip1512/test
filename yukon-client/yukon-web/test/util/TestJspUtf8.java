package util;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestJspUtf8 {
    /**
     * This test will search for jsps under 'yukonWebDirectory' and scan them verifying they can be mapped to UTF-8
     */
    private final String yukonWebDirectory = "C:/Dev/Workspace/trunk/yukon-web";

    @Test
    @Disabled("Cannot be run on build machine.")
    public void test() throws IOException {
        Files.walkFileTree(Paths.get(yukonWebDirectory), new FileVisitor<Path>() {
            CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();

            @Override
            public FileVisitResult visitFile(Path aFile, BasicFileAttributes aAttrs) throws IOException {
                String filename = aFile.getFileName().toString();
                if (!filename.endsWith(".jsp") && !filename.endsWith(".jspf")) {
                    return FileVisitResult.CONTINUE;
                }
                try (RandomAccessFile file = new RandomAccessFile(aFile.toFile(), "r")) {
                    ByteBuffer buffer =
                        file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.getChannel().size());
                    utf8Decoder.decode(buffer);
                } catch (MalformedInputException e) {
                    fail("Found character in " + aFile + " which isn't UTF-8.");
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
