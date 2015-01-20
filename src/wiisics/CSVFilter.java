package wiisics;

import org.apache.commons.io.FilenameUtils;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Created by funstein on 12/01/15.
 */
class CSVFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || FilenameUtils.isExtension(f.getAbsolutePath(), "csv"); //NON-NLS

    }

    @Override
    public String getDescription() {
        return WiisicsHandler.RESOURCE_BUNDLE.getString("export.data.csv");
    }
}
