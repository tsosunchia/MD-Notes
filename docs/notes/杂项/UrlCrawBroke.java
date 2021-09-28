import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 如果设置了自定义域名，将博客域名前缀填写入19行的变量userId中，点击运行
 */
public class UrlCrawBroke {
    static int maxPages = 20; // 填写你的博客查找页数
    static String userId = "hanquan";// 这里填入主页名称：例如主业为 https://hanquan.blog.csdn.net/ 则填入 hanquan 即可
    static final String homeUrl = "https://" + userId + ".blog.csdn.net/article/list/";

    static Set<String> urlSet = new HashSet<>();

    public static void getUrls() throws IOException, InterruptedException {
        InputStream is;
        String pageStr;
        StringBuilder curUrl = null;
        for (int i = 1; i < maxPages; i++) {
            Thread.sleep(300);
            System.out.println("正在查找第 " + i + " 页中的博客地址");
            curUrl = new StringBuilder(homeUrl);
            curUrl.append(i);
            System.out.println(curUrl);
            is = doGet(curUrl.toString());
            pageStr = inputStreamToString(is, "UTF-8");// 一整页的html源码

            List<String> list = getMatherSubstrs(pageStr, "(?<=href=\")https://hanquan.blog.csdn.net/article/details/[0-9]{8,9}(?=\")");
            urlSet.addAll(list);
            System.out.println("加入 " + list.size() + " 个url");
        }
    }

    public static void main(String urlstr[]) throws IOException, InterruptedException {
        // ----------------------------------------------遍历每一页 获取文章链接----------------------------------------------
        getUrls();

        // ---------------------------------------------------打印每个链接---------------------------------------------------
        System.out.println("打印每个链接");
        for (String s : urlSet) {
            System.out.println(s);
        }
        System.out.println("打印每个链接完毕");

        // ---------------------------------------------------多线程访问每个链接---------------------------------------------------
        ExecutorService executor = Executors.newCachedThreadPool();
        int threadCount = 3; // 并发线程数量
        for (int i = 0; i < threadCount; i++) {
            executor.execute(new MyThread(urlSet));
        }
        executor.shutdown();
    }

    public static InputStream doGet(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }

    public static String inputStreamToString(InputStream is, String charset) throws IOException {
        byte[] bytes = new byte[1024];
        int byteLength = 0;
        StringBuffer sb = new StringBuffer();
        while ((byteLength = is.read(bytes)) != -1) {
            sb.append(new String(bytes, 0, byteLength, charset));
        }
        return sb.toString();
    }

    // 正则匹配
    public static List<String> getMatherSubstrs(String str, String regex) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
}

class MyThread implements Runnable {
    public List<String> urlList;

    public MyThread(Set<String> urls) {
        List list = new ArrayList(urls);
        Collections.shuffle(list);
        this.urlList = list;
    }

    @Override
    public void run() {
        int i = 0;
        for (String s : urlList) {
            try {
                doGet(s);
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + "成功访问第" + (++i) + "个链接,共" + urlList.size() + "个:" + s);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static InputStream doGet(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36");
        InputStream inputStream = conn.getInputStream();
        return inputStream;
    }
}
