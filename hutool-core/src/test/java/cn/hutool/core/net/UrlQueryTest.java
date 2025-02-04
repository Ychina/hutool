package cn.hutool.core.net;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class UrlQueryTest {

	@Test
	public void parseTest(){
		String queryStr = "a=1&b=111==";
		UrlQuery q = new UrlQuery();
		UrlQuery parse = q.parse(queryStr, Charset.defaultCharset());
		assertEquals("111==", parse.get("b"));
		assertEquals("a=1&b=111==", parse.toString());
	}

	@Test
	public void ofHttpWithoutEncodeTest(){
		// charset为null表示不做编码
		String url = "https://img-cloud.voc.com.cn/140/2020/09/03/c3d41b93e0d32138574af8e8b50928b376ca5ba61599127028157.png?imageMogr2/auto-orient/thumbnail/500&pid=259848";
		final UrlBuilder urlBuilder = UrlBuilder.ofHttpWithoutEncode(url);
		final String queryStr = urlBuilder.getQueryStr();
		assertEquals("imageMogr2/auto-orient/thumbnail/500&pid=259848", queryStr);
	}

	@Test
	public void parseTest2(){
		String requestUrl = "http://192.168.1.1:8080/pc?=d52i5837i4ed=o39-ap9e19s5--=72e54*ll0lodl-f338868d2";
		UrlQuery q = new UrlQuery();
		UrlQuery parse = q.parse(requestUrl, Charset.defaultCharset());
		assertEquals("=d52i5837i4ed=o39-ap9e19s5--=72e54*ll0lodl-f338868d2", parse.toString());
	}

	@Test
	public void parseTest3(){
		// issue#1688@Github
		String u = "https://www.baidu.com/proxy";
		final UrlQuery query = UrlQuery.of(URLUtil.url(u).getQuery(), Charset.defaultCharset());
		assertTrue(MapUtil.isEmpty(query.getQueryMap()));
	}

	@Test
	public void parseTest4(){
		// https://github.com/dromara/hutool/issues/1989
		String queryStr = "imageMogr2/thumbnail/x800/format/jpg";
		final UrlQuery query = UrlQuery.of(queryStr, CharsetUtil.CHARSET_UTF_8);
		assertEquals(queryStr, query.toString());
	}

	@Test
	public void buildWithMapTest() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("username", "SSM");
		map.put("password", "123456");
		String query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("username=SSM&password=123456", query);

		map = new TreeMap<>();
		map.put("username", "SSM");
		map.put("password", "123456");
		query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("password=123456&username=SSM", query);
	}

	@Test
	public void buildHasNullTest() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put(null, "SSM");
		map.put("password", "123456");
		String query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("password=123456", query);

		map = new TreeMap<>();
		map.put("username", "SSM");
		map.put("password", "");
		query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("password=&username=SSM", query);

		map = new TreeMap<>();
		map.put("username", "SSM");
		map.put("password", null);
		query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("password&username=SSM", query);
	}

	@Test
	public void buildSpecialTest() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("key1&", "SSM");
		map.put("key2", "123456&");
		String query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("key1%26=SSM&key2=123456%26", query);

		map = new TreeMap<>();
		map.put("username=", "SSM");
		map.put("password", "=");
		query = URLUtil.buildQuery(map, StandardCharsets.UTF_8);
		assertEquals("password==&username%3D=SSM", query);
	}

	@Test
	public void plusTest(){
		// 根据RFC3986，在URL中，+是安全字符，即此符号不转义
		final String a = UrlQuery.of(MapUtil.of("a+b", "1+2")).build(CharsetUtil.CHARSET_UTF_8);
		assertEquals("a+b=1+2", a);
	}

	@Test
	public void parsePlusTest(){
		// 根据RFC3986，在URL中，+是安全字符，即此符号不转义
		final String a = UrlQuery.of("a+b=1+2", CharsetUtil.CHARSET_UTF_8)
				.build(CharsetUtil.CHARSET_UTF_8);
		assertEquals("a+b=1+2", a);
	}

	@Test
	public void spaceTest(){
		// 根据RFC3986，在URL中，空格编码为"%20"
		final String a = UrlQuery.of(MapUtil.of("a ", " ")).build(CharsetUtil.CHARSET_UTF_8);
		assertEquals("a%20=%20", a);
	}

	@Test
	public void parsePercentTest(){
		String queryStr = "a%2B=ccc";
		final UrlQuery query = UrlQuery.of(queryStr, null);
		assertEquals(queryStr, query.toString());
	}

	@Test
	public void parsePercentTest2(){
		String queryStr = "signature=%2Br1ekUCGjXiu50Y%2Bk0MO4ovulK8%3D";
		final UrlQuery query = UrlQuery.of(queryStr, null);
		assertEquals(queryStr, query.toString());
	}

	@Test
	public void issueI78PB1Test() {
		// 严格模式
		final UrlQuery query = new UrlQuery().setStrict(true);
		query.add(":/?#[]@!$&'()*+,;= ", ":/?#[]@!$&'()*+,;= ");

		final String string = query.build(CharsetUtil.CHARSET_UTF_8);
		assertEquals("%3A%2F%3F%23%5B%5D%40%21%24%26%27%28%29%2A%2B%2C%3B%3D%20=" +
			"%3A%2F%3F%23%5B%5D%40%21%24%26%27%28%29%2A%2B%2C%3B%3D%20", string);
	}
}
