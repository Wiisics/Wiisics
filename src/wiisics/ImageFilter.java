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
        return f.isDirectory() || FilenameUtils.isExtension(f.getAbsolutePath(), "png"); //NON-NLS

    }

    @Override
    public String getDescription() {
        return WiisicsHandler.RESOURCE_BUNDLE.getString("export.data.png");
    }
}
