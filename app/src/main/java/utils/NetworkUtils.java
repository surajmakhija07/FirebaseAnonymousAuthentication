package utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkUtils {

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     * *
     */
    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager mConMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return mConMgr.getActiveNetworkInfo() != null
                && mConMgr.getActiveNetworkInfo().isAvailable()
                && mConMgr.getActiveNetworkInfo().isConnected();
    }

    /**
     * Checks the type of data connection that is currently available on the device.
     *
     * @return <code>ConnectivityManager.TYPE_*</code> as a type of internet connection on the
     * device. Returns -1 in case of error or none of
     * <code>ConnectivityManager.TYPE_*</code> is found.
     * **
     */
    public static int getDataConnectionType(Context ctx) {

        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null && connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return ConnectivityManager.TYPE_MOBILE;
            } else if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return ConnectivityManager.TYPE_WIFI;
            } else
                return -1;
        } else
            return -1;
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        is.close();

        return sb.toString();
    }

    /***
     * Convert {@linkplain InputStream} to byte array.
     *
     * @throws NullPointerException If input parameter {@link InputStream} is null
     **/
    public static byte[] readStreamToBytes(InputStream inputStream) {

        if (inputStream == null)
            throw new NullPointerException("InputStream is null");

        byte[] bytesData = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            bytesData = buffer.toByteArray();

            // Log.d( TAG, "#readStream data: " + data );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();

                    if (inputStream != null)
                        inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }    // finally

        return bytesData;
    }

    /***
     * Convert {@linkplain InputStream} to byte array.
     *
     * @throws NullPointerException If input parameter {@link InputStream} is null
     **/
    public static String readStream(InputStream inputStream) {

        if (inputStream == null)
            throw new NullPointerException("InputStream is null");

        StringBuffer data = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            data = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
            // Log.d( TAG, "#readStream data: " + data );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();

                    if (inputStream != null)
                        inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }    // finally

        if (data == null)
            return null;
        else
            return data.toString();
    }

}