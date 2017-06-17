package proofs;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TestPermission {

	static String parentDir = "/";

	static Set<PosixFilePermission> defaultPosixPermissions = null;
	static {
		defaultPosixPermissions = new HashSet<>();
		defaultPosixPermissions.add(PosixFilePermission.OWNER_READ);
		defaultPosixPermissions.add(PosixFilePermission.OWNER_WRITE);
		defaultPosixPermissions.add(PosixFilePermission.OWNER_EXECUTE);
		defaultPosixPermissions.add(PosixFilePermission.GROUP_READ);
		defaultPosixPermissions.add(PosixFilePermission.GROUP_WRITE);
		// Others have read permission so that ftp user who doesn't belong to
		// the group can fetch the file
		defaultPosixPermissions.add(PosixFilePermission.OTHERS_READ);
		defaultPosixPermissions.add(PosixFilePermission.OTHERS_WRITE);
	}

	public static void createFileWithPermission(String fileName) throws IOException {
		// File parentFolder = new File(parentDir);
		// PosixFileAttributes attrs =
		// Files.readAttributes(parentFolder.toPath(),
		// PosixFileAttributes.class);
		// System.out.format("parentfolder permissions: %s %s %s%n",
		// attrs.owner().getName(),
		// attrs.group().getName(),
		// PosixFilePermissions.toString(attrs.permissions()));

		// FileAttribute<Set<PosixFilePermission>> attr =
		// PosixFilePermissions.asFileAttribute(attrs.permissions());
		File file = new File(fileName);
		FileAttribute<String> attr = new FileAttribute<String>() {

			@Override
			public String value() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String name() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		Files.createFile(file.toPath(),attr);
		Files.setPosixFilePermissions(file.toPath(), defaultPosixPermissions); // Assure
																				// the
																				// permissions
																				// again
																				// after
																				// the
																				// file
																				// is
																				// created
	}

	public static void main(String[] args) throws IOException {
		String fileName = parentDir + "testPermission_" + System.currentTimeMillis();
		createFileWithPermission(fileName);

	}

}