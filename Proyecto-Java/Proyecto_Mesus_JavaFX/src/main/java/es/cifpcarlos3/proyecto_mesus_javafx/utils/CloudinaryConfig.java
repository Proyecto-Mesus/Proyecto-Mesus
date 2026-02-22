package es.cifpcarlos3.proyecto_mesus_javafx.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

    private static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "duqflaaqz",
            "api_key", "947249116796376",
            "api_secret", "dL9nRVphZ1NDNBKSHW_MJtaGa4k"
    ));

    public static Cloudinary getCloudinary() {
        return cloudinary;
    }
}

