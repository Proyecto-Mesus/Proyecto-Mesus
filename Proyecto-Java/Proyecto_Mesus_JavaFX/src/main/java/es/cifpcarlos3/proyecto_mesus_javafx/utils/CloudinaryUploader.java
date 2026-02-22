package es.cifpcarlos3.proyecto_mesus_javafx.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class CloudinaryUploader {

    public static String uploadImage(File file) throws Exception {
        Cloudinary cloudinary = CloudinaryConfig.getCloudinary();

        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

        return uploadResult.get("secure_url").toString();
    }
}
