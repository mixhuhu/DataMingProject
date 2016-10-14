package com.morty.java.dmp.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by duliang on 2016/6/18.
 */

/**
 * Created by IntelliJ IDEA.
 * User: duliang
 * Date: 2016/6/18
 * Time: 23:12
 * email:duliang1128@163.com
 */
public class HadoopIO {
    Configuration conf;
    FileSystem fileSystem;

    /**
     * @param codecClassName ѹ���������
     * @param input          ����
     * @param output         ���
     */
    public void CompressStream(String codecClassName, InputStream input, OutputStream output) throws IOException {

        // TODO: 2016/6/18  ѹ���������ȡ������
        Class<?> codeClass = null;
        CompressionOutputStream out = null;

        try {
            codeClass = Class.forName(codecClassName);

            org.apache.hadoop.io.compress.CompressionCodec codec =
                    (org.apache.hadoop.io.compress.CompressionCodec) ReflectionUtils.newInstance(codeClass,
                            conf);
            Compressor compressor = null;

            /*
             * //todoѹ����
             *
             * compressor= CodecPool.getCompressor(codec);
             *  out=codec.createOutputStream(output,compressor);
             */
            out = codec.createOutputStream(output);
            IOUtils.copyBytes(input, out, 4096, false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    public void FileCompressor(String fileUri) throws Exception {

        // TODO: 2016/6/18  �����ļ���չ��ѡȡcodec��ѹ���ļ�
        fileSystem = FileSystem.get(URI.create(fileUri), conf);

        Path inputpath = new Path(fileUri);
        CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        org.apache.hadoop.io.compress.CompressionCodec codec = factory.getCodec(inputpath);

        if (codec == null) {
            System.err.print("no codec found for" + fileUri);
            System.exit(1);
        }

        String outputUri = CompressionCodecFactory.removeSuffix(fileUri, codec.getDefaultExtension());
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = codec.createInputStream(fileSystem.open(inputpath));
            outputStream = fileSystem.create(new Path(outputUri));
            IOUtils.copyBytes(inputStream, outputStream, conf);
        } finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

    public void init() {

        // TODO: 2016/6/18   ��ʼ������
        conf = new Configuration();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
