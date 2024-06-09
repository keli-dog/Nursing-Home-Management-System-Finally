package com.ew.gerocomium.common.config.file;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import com.ew.gerocomium.common.constant.Constant;
import com.ew.gerocomium.common.constant.ExceptionEnum;
import com.ew.gerocomium.common.util.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

/**
 * 文件信息
 */
@Configuration
@AllArgsConstructor
public class FileTempPath {
    private final FileUploadConfigPropertity fileUploadConfigPropertity;

    /**
     * 获取module存储路径
     *
     * @param module 模块名称
     * @return 根路径/module/当天日期/
     */
    public String uploadPath(String module) {
        return getRootPath() + File.separator
                + module + File.separator
                + DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN)
                + File.separator;
    }

    /**
     * 下载路径配置
     *
     * @return 根路径/download/
     */
    public String downloadPath() {
        return getRootPath() + File.separator + Constant.STR_DOWNLOAD + File.separator;
    }

    /**
     * 图片路径规则
     *
     * @return 根路径/upload/img/
     */
    public String imgSrcPath() {
        return getRootPath() + File.separator + Constant.STR_UPLOAD_IMG + File.separator;
    }

    /**
     * 视频路径规则
     *
     * @return 根路径/upload/video/
     */
    public String videoSrcPath() {
        return getRootPath() + File.separator + Constant.STR_UPLOAD_VIDEO + File.separator;
    }

    /**
     * 上传附件路径规则
     *
     * @return 根路径/upload/file/
     */
    public String filePath() {
        return getRootPath() + File.separator + Constant.STR_UPLOAD_FILE + File.separator;
    }

    /**
     * 获取根路径
     *
     * @return 根路径
     */
    public String getRootPath() {
        OsInfo osInfo = new OsInfo();
        if (osInfo.isWindows()) {
            return fileUploadConfigPropertity.getWindows();
        } else if (osInfo.isMacOsX()) {
            return fileUploadConfigPropertity.getMacos();
        } else {
            return fileUploadConfigPropertity.getLinux();
        }
    }

    /**
     * 文件上传 保存
     *
     * @param file
     * @param module
     * @param suff
     * @return
     */
    @SneakyThrows
    public File saveFile(MultipartFile file, String module, String[] suff) {
        if (file == null || StrUtil.isBlank(file.getOriginalFilename())) {
            return null;
        }
        String suffix = StrPool.DOT + FileUtil.getSuffix(file.getOriginalFilename());
        // 文件后缀校验
        AssertUtil.isTrue(!StrUtil.equalsAnyIgnoreCase(suffix, suff), ExceptionEnum.UPLOAD_SUFFIX_ERROR);
        // 文件转存
        // 模块路径/雪花算法ID_上传文件名称
        String fileName = IdUtil.getSnowflakeNextIdStr() + "_" + file.getOriginalFilename();
        File files = FileUtil.file(uploadPath(module) + fileName);
        FileUtil.mkParentDirs(files);

        file.transferTo(files);
        return files;
    }


    /**
     * 文件上传 备查信息
     *
     * @param file http MultipartFile 文件流
     * @param suff 后缀 包含此类型的后缀不允许上传 如：.exe .sh
     * @return
     */
    @SneakyThrows
    public File saveInformationFile(MultipartFile file, String[] suff) {
        return saveFile(file, "information", suff);
    }

    @SneakyThrows
    public File saveCommFile(MultipartFile file, String module) {
        return saveFile(file, module, null);
    }

    /**
     * 下载路径处理 前提是放开uri
     *
     * @param file
     * @return
     */
    public String downloadUrl(File file) {
        String filePath = file.getAbsolutePath();
        String downloadUrl = filePath
                .replace(getRootPath(), "")
                .replace(StrPool.BACKSLASH, StrPool.SLASH);
        return CharPool.SLASH + downloadUrl;
    }

    public String downloadUrl(String path) {
        return downloadUrl(FileUtil.file(path));
    }

}