package com.gysoft;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class OfficeToPDF {

    private final static Logger logger = LoggerFactory.getLogger(OfficeToPDF.class);

    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;
    private static final int msoTrue = -1;
    private static final int msofalse = 0;


    @Test
    public  void  testSystem(){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            System.out.println(os + " can't gunzip");
        }
    }

    @Test
    public  void convert2PDF(){
        long start = System.currentTimeMillis();
        word2PDF("C:\\Users\\DELL\\Desktop\\aaa.ppt","C:\\Users\\DELL\\Desktop\\a.pdf");
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

    @Test
    public   void  testPPT2PDF() throws IOException {
        convert2PDF("C:\\Users\\DELL\\Desktop\\aaa.ppt","C:\\Users\\DELL\\Desktop\\1.pdf");
    }

    //ppt转pdf
    public static boolean convert2PDF(String inputFile, String pdfFile) throws IOException {
        String suffix =  getFileSufix(inputFile);
        File file = new File(inputFile);
        if(!file.exists()){
            System.out.println("文件不存在！");
            return false;
        }
        if(suffix.equals("pdf")){
            System.out.println("PDF not need to code!");
            return false;
        }
        if(suffix.equals("doc")||suffix.equals("docx")||suffix.equals("txt")||suffix.equals("wps")){
            return word2PDF(inputFile,pdfFile);
        }else if(suffix.equals("ppt")||suffix.equals("pptx")||suffix.equals("pot")||suffix.equals("pps")){
            return ppt2PDF(inputFile,pdfFile);
        }else if(suffix.equals("xls")||suffix.equals("xlsx")){
            return excel2PDF(inputFile,pdfFile);
        }else{
            System.out.println("文件格式不支持转换!");
            return false;
        }
    }

    public static String getFileSufix(String fileName){
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }


    public static boolean word2PDF(String inputFile,String pdfFile){
        try{
            //打开word应用程序
            ActiveXComponent app = new ActiveXComponent("Word.Application");
            //设置word不可见
            app.setProperty("Visible", false);
            //获得word中所有打开的文档,返回Documents对象
            Dispatch docs = app.getProperty("Documents").toDispatch();
            //调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            Dispatch doc = Dispatch.call(docs,
                    "Open",
                    inputFile,
                    false,
                    true
            ).toDispatch();
            //调用Document对象的SaveAs方法，将文档保存为pdf格式

            Dispatch.call(doc,
                    "SaveAs",
                    pdfFile,
                    wdFormatPDF     //word保存为pdf格式宏，值为17
            );

            Dispatch.call(doc,
                    "ExportAsFixedFormat",
                    pdfFile,
                    wdFormatPDF     //word保存为pdf格式宏，值为17
            );
            //关闭文档
            Dispatch.call(doc, "Close",false);
            //关闭word应用程序
            app.invoke("Quit", 0);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static boolean ppt2PDF(String inputFile,String pdfFile){
        try{

             ActiveXComponent app = new ActiveXComponent("KWPP.Application");
           // ActiveXComponent app = new ActiveXComponent("PowerPoint.Application");
            //app.setProperty("Visible", msofalse);
            Dispatch ppts = app.getProperty("Presentations").toDispatch();

            Dispatch ppt = Dispatch.call(ppts,
                    "Open",
                    inputFile,
                    true,//ReadOnly
                    true,//Untitled指定文件是否有标题
                    false//WithWindow指定文件是否可见
            ).toDispatch();

            Variant saveAs = Dispatch.call(ppt,
                    "SaveAs",
                    pdfFile,
                    ppSaveAsPDF
            );


            Dispatch.call(ppt, "Close");

            app.invoke("Quit");
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public void wordToPDF(String docFileName){

        System.out.println("启动Word...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();

            //  String path =  this.getSession().getServletContext().getRealPath("/")+"attachment/";
            String path="C:\\Users\\DELL\\Desktop";
            String sfileName = path+ docFileName + ".doc";
            String toFileName = path+ docFileName + ".pdf";

            doc = Dispatch.call(docs,  "Open" , sfileName).toDispatch();
            System.out.println("打开文档..." + sfileName);
            System.out.println("转换文档到PDF..." + toFileName);
            File tofile = new File(toFileName);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc,
                    "SaveAs",
                    toFileName, // FileName
                    wdFormatPDF);
            long end = System.currentTimeMillis();
            System.out.println("转换完成..用时：" + (end - start) + "ms.");


        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc,"Close",false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[] {});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();

    }



    @Test
    public void testExcelToPDF() throws IOException {
        excel2PDF("C:\\Users\\DELL\\Desktop\\a5564ded3074c69672bab5c178ae850e.xlsx","C:\\Users\\DELL\\Desktop\\saaaa.pdf");
    }

    public static boolean excel2PDF(String inputFile,String pdfFile) throws IOException {

        //文件的校验


        try{
            ComThread.InitSTA();
            ActiveXComponent app = new ActiveXComponent("Excel.Application");
            app.setProperty("Visible", false);
            Dispatch excels = app.getProperty("Workbooks").toDispatch();
            Dispatch excel = Dispatch.call(excels,
                    "Open",
                    inputFile,
                    false,
                    true
            ).toDispatch();
            //设置打印属性并打印
                      /* Dispatch.callN(excel,"PrintOut",new Object[]{Variant.VT_MISSING, Variant.VT_MISSING,  new Integer(1),
                                         new Boolean(false),"outlook", new Boolean(true),Variant.VT_MISSING, ""});*/
            Dispatch.call(excel,
                    "ExportAsFixedFormat",

                    xlTypePDF,
                    pdfFile
            );
           /* Variant call = Dispatch.call(excel,
                    "ExportAsFixedFormat",
                    xlTypePDF,
                    pdfFile, 0, false, true
            );*/
            Dispatch.call(excel, "Close",false);
            app.invoke("Quit");
            return true;
        }catch(Exception e){
            ComThread.Release();
            e.printStackTrace();
            return false;
        }
    }

/*
  public  void tt(){
         logger.info("启动Word..");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        File tofile = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();

            //String path =  this.getSession().getServletContext().getRealPath("/")+"attachment/";

            doc = Dispatch.call(docs, "Open", sPath).toDispatch();
            logger.info("打开文档...");
            logger.info("转换文档到PDF..." + toPath);
            tofile = new File(toPath);
            if(null!=tofile){
                tofile.delete();
            }
            if (tofile.exists()) {

            }
            Dispatch.call(doc,
                    "SaveAs",
                    toPath, // FileName
                    JacobConstant.WDFORMATPDF
            );

            long end = System.currentTimeMillis();
        logger.info("转换完成..用时：" + (end - start) + "ms.");
        } catch (Exception e) {
            logger.info("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc, "Close", false);
          logger.info("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        //如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
    }*/





}
