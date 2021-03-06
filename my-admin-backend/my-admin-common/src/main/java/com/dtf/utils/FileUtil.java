package com.dtf.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.dtf.exception.BadRequestException;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/13 1:39
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 系统临时目录
     * <br>
     * windows 包含路径分割符，但Linux 不包含,
     * 在windows \\==\ 前提下，
     * 为安全起见 同意拼装 路径分割符，
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;

    private static final int GB = 1024 * 1024 * 1024;

    private static final int MB = 1024 * 1024;

    private static final int KB = 1024;

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static final String IMAGE = "图片";
    public static final String TXT = "文档";
    public static final String MUSIC = "引言";
    public static final String VIDEO = "视频";
    public static final String OTHER = "其他";

    public static File toFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            file = new File(SYS_TEM_DIR + IdUtil.simpleUUID() + prefix);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 扩展名
     *
     * @param filename \
     * @return \
     */
    public static String getExtensionName(String filename) {
        if (StringUtils.isNoneBlank(filename)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length()) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = SYS_TEM_DIR + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);
        writer.write(list, true);
        SXSSFSheet sheet = (SXSSFSheet) writer.getSheet();
        sheet.trackAllColumnsForAutoSizing();
        writer.autoSizeColumnAll();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        file.deleteOnExit();
        writer.flush(outputStream, true);
        IoUtil.close(outputStream);
    }

    public static void checkSize(long maxSize, long size) {
        int m = 1024 * 1024;
        if (size > (maxSize * m)) {
            throw new BadRequestException("文件大小不能超过: " + maxSize + "MB.");
        }
    }

    public static String getSize(long size) {
        String result;
        if (size / GB >= 1) {
            result = DF.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {
            result = DF.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {
            result = DF.format(size / (float) KB) + "KB   ";
        } else {
            result = size + "B   ";
        }
        return result;
    }

    public static File upload(MultipartFile file, String filePath) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
        String name = getFileNameNoEx(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + format.format(date);
        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            File dest = new File(path).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    System.out.println("create dir failed.");
                }
            }
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
