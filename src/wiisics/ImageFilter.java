package wiisics;

import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by funstein on 12/01/15.
 */
class ImageFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        return FilenameUtils.isExtension(f.getAbsolutePath(), "png");

    }

    @Override
    public String getDescription() {
        return "Export Graphs as Image (.png)";
    }
}
