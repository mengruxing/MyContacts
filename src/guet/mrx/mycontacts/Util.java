package guet.mrx.mycontacts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Util {
	public static final int[] material_color = { 0xFFE51C23, 0xFFE91E63,
			0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5, 0xFF5677FC, 0xFF03A9F4,
			0xFF00BCD4, 0xFF009688, 0xFF259B24, 0xFF8BC34A, 0xFFCDDC39,
			0xFFFFED3B, 0xFFFFC107, 0xFFFF9800, 0xFFFF5722, 0xFF795548,
			0xFF9E9E9E, 0xFF607D8B };

	/**
	 * 从TXT文件中导入数据
	 * 
	 * @param context
	 *            上下文
	 * @param table
	 *            表名
	 * @param path
	 *            文件路径
	 * @param encoding
	 *            编码格式
	 * @return 是否成功
	 * @throws IOException
	 *             异常
	 */
	public static boolean importFromTXT(Context context, String table,
			String path, String encoding) throws IOException {
		DAO dao = new DAO(context);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), encoding));
		String lineText;
		List<Person> persons = new ArrayList<Person>();
		while ((lineText = bufferedReader.readLine()) != null) {
			StringBuilder nameBuilder = new StringBuilder();
			String number = "";
			if ((!lineText.isEmpty())&&(lineText.charAt(0) != '#')) {
				String[] strings = lineText.split(" +");// 目前过滤：空格
				for (String string : strings) {
					if (!isStringLong(string)) {
						nameBuilder.append(string);
					} else {
						number = string;
					}
				}
				String name = nameBuilder.toString();
				if (!(name.length() == 0 || number.length() > 15)) {
					Person person = new Person(name, number);
					persons.add(person);
				}
			}
		}
		bufferedReader.close();
		dao.insert(table, persons);
		return true;
	}

	public static boolean importFromExcel(Context context, String tableName,
			String filePath) throws BiffException, IOException {
		DAO dao = new DAO(context);
		Workbook workbook = Workbook.getWorkbook(new File(filePath));
		Sheet[] sheets = workbook.getSheets();
		Sheet sheet = sheets[0];
		int rows = sheet.getRows();
		int columns = sheet.getColumns();
		String[][] tableStrings = new String[rows][columns];
		int nameRow = 0, nameColumn = 0, numRow = 0, numColumn = 1;
		for (int i = rows-1; i >=0; i--) {
			for (int j = columns-1; j >=0; j--) {
				Cell cell = sheet.getCell(j, i);
				tableStrings[i][j] = cell.getContents();
				System.out.print(tableStrings[i][j] + " ");
				switch (tableStrings[i][j]) {
				case "姓名":
					nameRow = i;
					nameColumn = j;
					break;
				case "手机号码":
				case "电话号码":
				case "联系电话":
				case "联系方式":
					numRow = i;
					numColumn = j;
					break;
				}
			}
			System.out.println();
		}
		if (nameRow == numRow) {
			List<Person> persons = new ArrayList<Person>();
			for (int i = nameRow + 1; i < tableStrings.length; i++) {
				persons.add(new Person(tableStrings[i][nameColumn],
						tableStrings[i][numColumn]));
			}
			dao.insert(tableName, persons);
		}
		workbook.close();
		return true;
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isStringLong(String string) {
		try {
			Long.parseLong(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 是否是目录
	 */
	public static boolean isDirectory(String filePath) {
		File file = new File(filePath);
		return file.isDirectory();
	}

	/**
	 * 是否符合格式
	 */
	public static boolean isFileFormat(String filePath, String format) {
		String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
		return suffix.equalsIgnoreCase(format);
	}

	/**
	 * 是否存在
	 */
	public static boolean isFileExists(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * 检测文件是存在，并且不为目录，且为符合要求的后缀名
	 */
	public static boolean checkFilePath(String filePath, String format) {
		if (!isFileExists(filePath) || isDirectory(filePath))
			return false;
		return isFileFormat(filePath, format);
	}

	public static boolean isNormalDir(File dirFile) {
		return dirFile.isDirectory()
				&& !dirFile.getName().substring(0, 1).equals(".");
	}

	/**
	 * 获取SD卡根目录
	 */
	public static String getSDDir() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e("getSDDir", "无法获取SD目录");
			return null;
		}
		return Environment.getExternalStorageDirectory().toString();

	}
}