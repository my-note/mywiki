[https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247494717&idx=1&sn=e73fe020e67c1e92ca03e27630151aa5&chksm=e908d897de7f51816cc4ab7a5167fe02689ad71e8d670f4666e83041f725aa568704be6ae424&cur_album_id=1914508501259730944&scene=189#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247494717&idx=1&sn=e73fe020e67c1e92ca03e27630151aa5&chksm=e908d897de7f51816cc4ab7a5167fe02689ad71e8d670f4666e83041f725aa568704be6ae424&cur_album_id=1914508501259730944&scene=189#wechat_redirect) 

> 真正的程序员认为自己比用户更明白用户需要什么。本文已被https://yourbatman.cn收录；公号后台回复“**专栏列表**”获取全部小而美的**原创**技术专栏

你好，我是**YourBatman**。

依稀记得3年前的在“玩”Spring WebFlux的时候，看到`PathPattern`在AbstractHandlerMapping中起到了重要作用：用于URL的匹配。当时就很好奇：这一直不都是AntPathMatcher的活吗？

于是乎我就拿出了自己更为熟悉的Spring WebMvc对于类进行功能比对，发现PathPattern扮演的角色和AntPathMatcher一毛一样，所以当时也就没去深入研究啦。

正所谓**念念不忘必有回响**。时隔3年最近又回到搞WebFlux了，欠下的债总归要还呀，有必要把PathPattern深入解读，毕竟它是Spring5在路径解析器方面的**新宠**，贯穿WebFlux上下。重点是号称比AntPathMatcher拥有更好的使用体验以及更快的匹配效率，咦，勾起了兴趣了解一下~

正值周末，说干就干。

## 所属专栏

-   [点拨-Spring技术栈](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=MzI0MTUwOTgyOQ==&action=getalbum&album_id=1914508501259730944#wechat_redirect)

## 本文提纲

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBOM3uIl2eNgA14AYtibYo4uf5TvCvG1trqk2kAQNlOGfw8XS95dMWl5A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

## 版本约定

-   JDK：**8**
-   Spring Framework：**5.3.x**

PathPattern是Spring5新增的API，所在包：`org.springframework.web.util.pattern.PathPattern`，所属模块为`spring-web`。可见它专为Web设计的“工具”。

不同于AntPathMatcher是一个“上帝类”把所有活都干了，新的路径匹配器围绕着PathPattern拥有一套体系，在设计上更具模块化、更加面向对象，从而拥有了更好的**可读性**和可扩展性。

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBZ5NMiccecbUz3ZUaoPc2u0zeI0YV41J2jH5lFqX8CRljwt51Gvgvwgg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

下面深入了解下该技术体系下的核心元素。主要有：

-   **PathElement**：路径元素。一个URL模板根据/可以拆分成N多个路径元素对象
-   **PathContainer**：URL的结构化表示。一个URL对应一个PathContainer对象实例
-   **PathPattern**：路径解析的模式。路径模式匹配器的最核心API
-   **PathPatternParser**：将一个String类型的模式解析为PathPattern实例，这是创建PathPattern实例的**唯一**方式

## PathElement：路径元素

顾名思义，它表示路径节点。一个path会被解析成N多个PathElement节点。

核心属性：

```java
// Since: 5.0  
abstract class PathElement {

 protected final int pos;  
 protected final char separator;  
 @Nullable  
 protected PathElement next;  
 @Nullable  
 protected PathElement prev;  
}

```

-   pos：该节点在path里的起点位置
-   separator：该path使用的分隔符
-   next：后节点，可以为null（如最后一个节点）
-   prev：前节点，可以为null（如第一个节点）

所有的PathElement之间形成**链状**结构，构成一个完整的URL模板。

> Tips：我个人意见，并不需要太深入去了解PathElement内部的具体实现，在宏观角度了解它的定义，然后认识下它的子类实现不同的节点类型即可

它有如下子类实现：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibB4oFknLKNO4zyG9hUCx1fokLPbS8VTsTjGNGgfWTtkoQuH2OEUk7JUQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

#### SeparatorPathElement

分离器元素。代表用于分离的元素（默认是`/`，也可以是`.`）

```java
@Test  
public void test1() {  
    PathPatternParser parser = new PathPatternParser();  
    PathPattern pathPattern = parser.parse("/api/v1");  
    System.out.println(pathPattern);  
}  
```

断点调试查看解析后的`pathPattern`变量拥有的元素情况：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBozyFBb5Nt7ibm011gomzy1eMaMhibuUsP33VrvWqSARJsVHyT1khEE2Q/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

可以看到这是标准的链式结构嘛，这种关系用图画出来就是这样子：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBia8nqqrXYRhZc6V6coKfJzSZpTib1kYPA5hia64Cc3cN2yqZT6k3h9FrQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

其中绿色的`/`都是SeparatorPathElement类型，蓝色都是LiteralPathElement字面量类型。将一个Pattern拆解成为了一个个的Element对象，后面就可以方便的**面向对象**编程，大大增加了可读性、降低出错的概率。

说明：由于这是第一个元素，所以才举了个实际的代码示例辅助理解。下面的就只需描述概念啦，举一反三即可~

#### WildcardPathElement

通配符元素。如：`/api/*/yourbatman`

说明：在路径中间它至少匹配1个字符（`//`不行，`/ /`可行），但在路径末尾可以匹配0个字符

#### SingleCharWildcardedPathElement

单字符通配符元素。如：/api/your??tman 

说明：一个?代表一个单字通配符，若需要适配多个用多个?即可

#### WildcardTheRestPathElement

通配剩余路径元素。如：`/api/yourbatman/**` 

说明：`**`只能放在path的末尾，这才是rest剩余的含义嘛

#### CaptureVariablePathElement

将一段路径**作为变量**捕获的路径元素。如：/api/yourbatman/{age} 

说明：{age}就代表此元素类型被封装进来

#### CaptureTheRestPathElement

捕获路径**其余部分**的路径元素。如：/api/yourbatman/{*restPath} 

说明：若待匹配的路径是`/api/yourbatman/a/b/c`，那么restPath=a/b/c

#### LiteralPathElement

字面量元素。不解释~

#### RegexPathElement

正则表达式元素。如：`api/*_*/*_{age}`

说明：`*_*`和`*_{age}`都会被解析为该元素类型，这种写法是从AntPathMatcher里派生来过的（但不会依赖于AntPathMatcher）

**总之**：任何一个字符串的pattern最终都会被解析为若干段的PathElement，这些PathElement以链式结构连接起来用以表示该pattern，形成一个对象数据。不同于AntPathMatcher的纯字符串操作，这里把每一段都使用对象来描述，**结构化**的表示使得可读性更强、更具灵活性，甚至可以获得更好的性能表现。

## PathContainer：URL的结构化表示

和PathPattern类似，待匹配的path的每一段都会表示为一个元素并保存其元数据信息。也就是说：每一个待匹配的URL路径都会被解析为一个PathContainer实例。

PathContainer虽然是个接口，但我们无需关心其实现，类同于Java 8的`java.util.stream.Collector`接口使用者无需关心其实现一样。因为提供了**静态工具方法**用于直接生成对应实例。体验一把：

```java
@Test  
public void test2() {  
    PathContainer pathContainer = PathContainer.parsePath("/api/v1/address", PathContainer.Options.HTTP_PATH);  
    System.out.println(pathContainer);  
}  
```

debug模式运行，查看pathContainer对象详情：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibB7U2MibHrBCgbPgwWp5LBgZpxZEkfuHyg4qCFA7e3DbyQfgsZ2dojCAw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

这和解析为PathPattern的结构何其相似（不过这里元素们是通过**有序的**集合组织起来的）。对比看来，拍脑袋应该能够猜到何新版的匹配效率会更高了吧。

补充说明：

-   value和valueToMatch的区别：value是原值，valueToMatch是（处理过的，比如已解码的）最终参与匹配的值
-   parameters代表路径参数。若希望它有值只需使用`;`号分隔填值即可。如：`/api;abc/v1`，此参数一般都用不着
-   因为Http中是允许这样携带参数的，但是目录（`.`形式）就不能这么写啦

## PathPattern：路径解析的模式

表示解析路径的模式。包括用于快速匹配的路径元素链，并**累积**用于快速比较模式的计算状态。它是直接面向使用者进行匹配逻辑的最重要API，完成match操作。

PathPattern所在包是`org.springframework.web.util.pattern.PathPattern`，位于spring-web模块，专为web（含webmvc和webflux）设计的全新一套路径匹配API，具有更高的匹配效率。

认识下它的成员属性：

```java
// Since: 5.0  
public class PathPattern implements Comparable<PathPattern> {

 // pattern的字符串形式  
 private final String patternString;  
 // 用于构建本实例的解析器  
 private final PathPatternParser parser;  
 // 分隔符使用/还是.，默认是/  
 private final PathContainer.Options pathOptions;  
 // 如果pattern里结尾没/而待匹配的有，仍然让其匹配成功（true），默认是true  
 private final boolean matchOptionalTrailingSeparator;  
 // 是否对大小写敏感，默认是true  
 private final boolean caseSensitive;  
 // 链式结构：表示URL的每一部分元素  
 @Nullable  
 private final PathElement head;

 private int capturedVariableCount;  
 private int normalizedLength;  
 private boolean endsWithSeparatorWildcard = false;  
 private int score;  
 private boolean catchAll = false;

}

```

以上属性是直接读取，下面这些个是计算出来的，比较特殊就特别照顾下：

-   **capturedVariableCount**：在这个模式中捕获的变量总数。也就是{xxx}或者正则捕获的总数喽
-   **normalizedLength**：通配符批到的变量长度的总和（关于长度的计算有个约定：如?是1，字面量就是字符串长度），这个变量对提升匹配速度有帮助
-   **endsWithSeparatorWildcard**：标记该模式是否以隔离符或者通配符\*结尾
-   **score**：分数用于快速比较该模式。不同的模式组件被赋予不同的权重。分数越低越具体，如：捕获到的变量分数值为1，通配符值是100
-   **catchAll**：该pattern是否以\*\*或者{\*xxx}结尾

> score、catchAll等标记用于加速匹配的速度，具体体现在`PathPattern.SPECIFICITY_COMPARATOR`这个比较器上，这是PathPattern速度比AntPathMatcher快的根因之一

值得注意的是：所有属性均不提供public的set方法，也就是说PathPattern实例一旦创建就是只读（不可变）实例了。

### 快速创建缺省的实例

上面了解到，PathPattern的构造器不是public的，所以有且仅能通过`PathPatternParser`创建其实例。然而，为快速满足绝大多数场景，Spring还提供了一种快速创建缺省的PathPattern实例的方式：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBoys8d6j4BibY9ib3hQPDNEibDtWr4uouERrlj5M7ibg2IMX5c5NVRrTPkQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

PathPatternParser提供一个全局共享的、只读的实例用于快速创建**缺省的PathPattern实例**，类似于实例工厂的作用。毕竟绝大部分场景下用PathPattern的缺省属性即可，因此有了它着实方便不少。

> 注意：虽然该PathPatternParser实例是全局共享只有1个，**但是**，创建出来的PathPattern可是不同实例哦（基本属性都一样而已）

### 代码示例

PathPattern的匹配方式和AntPathMatcher基本保持一致：使用的基于Ant风格模式匹配。

> 但是发现没，这里不再强调Ant字样，也许Spring觉得Ant的概念确实已廉颇老矣？不符合它紧跟潮流的身份？

相比于AntPathMatcher，PathPattern主要有两处地方不一样：

> 说明：PathPattern只支持两种分隔符（/和.），而AntPathMatcher可以随意指定。虽然这也是不同点，但这一般无伤大雅所以就不单独列出了

#### 1. 新增{*pathVariable}语法支持

这是PathPattern新增的“语法”，表示匹配余下的path路径部分并将其赋值给pathVariable变量。

```java
@Test  
public void test1() {  
    System.out.println("======={*pathVariable}语法======");  
    PathPattern pattern = PathPatternParser.defaultInstance.parse("/api/yourbatman/{*pathVariable}");

    // 提取匹配到的的变量值  
    System.out.println("是否匹配：" + pattern.matches(PathContainer.parsePath("/api/yourbatman/a/b/c")));  
    PathPattern.PathMatchInfo pathMatchInfo = pattern.matchAndExtract(PathContainer.parsePath("/api/yourbatman/a/b/c"));  
    System.out.println("匹配到的值情况：" + pathMatchInfo.getUriVariables());  
}

======={*pathVariable}语法======  
是否匹配：true  
匹配到的值情况：{pathVariable=/a/b/c}

```

在没有PathPattern之前，虽然也可以通过`/**`来匹配成功，但却无法得到匹配到的值，现在可以了！

##### 和`**`的区别

我们知道`/**`和`/{*pathVariable}`都有匹配剩余所有path的“能力”，那它俩到底有什么区别呢？

1.  `/**`能匹配成功，但无法获取到动态成功匹配元素的值
2.  `/{*pathVariable}`可认为是`/**`的加强版：可以获取到这部分动态匹配成功的值

正所谓一代更比一代强嘛，如是而已。

##### 和`**`的优先级关系

既然`/**`和`/{*pathVariable}`都有匹配剩余path的能力，那么它俩若放在一起，优先级关系是怎样的呢？

妄自猜测没有意义，跑个案例一看便知：由于PathPattern实现了比较器接口，因此本例利用SortedSet自动排序即可，**排第一的证明优先级越高**

```java
@Test  
public void test2() {  
    System.out.println("======={*pathVariable}和/**优先级======");  
    PathPattern pattern1 = PathPatternParser.defaultInstance.parse("/api/yourbatman/{*pathVariable}");  
    PathPattern pattern2 = PathPatternParser.defaultInstance.parse("/api/yourbatman/**");

    SortedSet<PathPattern> sortedSet = new TreeSet<>();  
    sortedSet.add(pattern1);  
    sortedSet.add(pattern2);

    System.out.println(sortedSet);  
}

======={*pathVariable}和/**优先级======  
[/api/yourbatman/**, /api/yourbatman/{*pathVariable}]  


```

测试代码的细节：故意将`/{*pathVariable}`先放进set里面而后放`/**`，但最后还是`/**`在前。

结论：当二者同时出现（出现冲突）时，`/**`优先匹配。

#### 2. 禁用中间`**`语法支持

在上篇文章对AntPathMatcher的详细分析文章中，我们知道是可以把`/**`放在整个URL中间用来匹配的，如：

```java
@Test  
public void test4() {  
    System.out.println("=======**:匹配任意层级的路径/目录=======");  
    String pattern = "/api/**/yourbatman";

    match(1, MATCHER, pattern, "/api/yourbatman");  
    match(2, MATCHER, pattern, "/api//yourbatman");  
    match(3, MATCHER, pattern, "/api/a/b/c/yourbatman");  
}

=======**:匹配任意层级的路径/目录=======  
1 match结果：/api/**/yourbatman 【成功】 /api/yourbatman  
2 match结果：/api/**/yourbatman 【成功】 /api//yourbatman  
3 match结果：/api/**/yourbatman 【成功】 /api/a/b/c/yourbatman  


```

与`AntPathMatcher`不同，`**`仅在模式**末尾**受支持。中间不被允许了，否则实例创建阶段就会报错：

```java
@Test  
public void test3() {  
    System.out.println("=======/**放在中间语法======");  
    PathPattern pattern = PathPatternParser.defaultInstance.parse("/api/**/yourbatman");

    pattern.matches(PathContainer.parsePath("/api/a/b/c/yourbatman"));  
}

=======/**放在中间语法======  
org.springframework.web.util.pattern.PatternParseException: No more pattern data allowed after {*...} or ** pattern element  
 at org.springframework.web.util.pattern.InternalPathPatternParser.peekDoubleWildcard(InternalPathPatternParser.java:250)  
 ...  


```

> 从报错中还能看出端倪：不仅`**`，`{*xxx}`也是不能放在中间而只能是末尾的

PathPattern这么做的目的是：**消除歧义**。

那么问题来了，如果就是想匹配**中间的**任意层级路径怎么做呢？答：首先这在web环境里有这样需求的概率极小（PathPattern只适用于web环境），若这依旧是刚需，那就只能蜕化到借助AntPathMatcher来完成喽。

## PathPattern对比AntPathMatcher

二者目前都存在于Spring技术栈内，做着“相同”的事。虽说现在还鲜有同学了解到PathPattern，我认为淘汰掉AntPathMatcher只是时间问题（特指web环境哈），毕竟后浪总归有上岸的一天。

但不可否认，二者将在较长时间内共处，那么它俩到底有何区别呢？了解一下

### 出现时间

`AntPathMatcher`是一个早在2003年（Spring的第一个版本）就已存在的路径匹配器，而PathPattern是**Spring 5**新增的，旨在用于替换掉较为“古老”的AntPathMatcher。

### 功能差异

PathPattern去掉了Ant字样，但保持了很好的向下兼容性：除了不支持将`**`写在path中间之外，其它的匹配规则从行为上均保持和AntPathMatcher一致，并且还新增了强大的`{*pathVariable}`的支持。

因此在功能上姑且可认为二者是一致的，极特殊情况下的不兼容除外。

### 性能差异

Spring官方说PathPattern的性能优于AntPathMatcher，我抱着怀疑的态度做了测试，示例代码和结果如下：

```java
// 匹配的模板：使用一个稍微有点复杂的模板进行测试  
private static final String pattern = "/api/your?atman/{age}/**";  
`

`// AntPathMatcher匹配代码：使用单例的PathMatcher，符合实际使用情况  
private static final PathMatcher MATCHER = new AntPathMatcher();  
public static void antPathMatcher(String reqPath) {  
    MATCHER.match(reqPath);  
}  
`

`// PathPattern代码示例：这里的pattern由下面来定义  
private static final PathPattern PATTERN = PathPatternParser.defaultInstance.parse(pattern);  
public static void pathPattern(String reqPath) {  
    PATTERN.matches(PathContainer.parsePath(reqPath));  
}  
```

匹配的测试代码：

```java
@Test  
public void test1() {  
    Instant start = Instant.now();  
    for (int i = 0; i < 100000; i++) {  
        String reqPath = "/api/yourBatman/" + i + "/" + i;  
        antPathMatcher(reqPath);  
        // pathPattern(reqPath);  
    }  
    System.out.println("耗时(ms)：" + Duration.between(start, Instant.now()).toMillis());  
}  
```

不断调整循环次数，且各执行三次，将结果绘制成如下表格：

> 测试机配置为：

![图片](https://mmbiz.qpic.cn/mmbiz_png/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBqgiat8YQjcDPRibxiaqhNzfT3a5GgEomzIl7mk9plEfFwdy8shqLxn4Sw/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

循环100000次：

| 路径匹配器     | 第1次耗时 | 第2次耗时 | 第3次耗时 |
|----------------|-----------|-----------|-----------|
| AntPathMatcher | 171       | 199       | 188       |
| PathPattern    | 118       | 134       | 128       |

循环1000000次：

| 路径匹配器     | 第1次耗时 | 第2次耗时 | 第3次耗时 |
|----------------|-----------|-----------|-----------|
| AntPathMatcher | 944       | 852       | 882       |
| PathPattern    | 633       | 637       | 626       |

循环10000000次：

| 路径匹配器     | 第1次耗时 | 第2次耗时 | 第3次耗时 |
|----------------|-----------|-----------|-----------|
| AntPathMatcher | 5561      | 5469      | 5461      |
| PathPattern    | 4495      | 4440      | 4571      |

**结论：PathPattern性能比AntPathMatcher优秀。理论上pattern越复杂，PathPattern的优势越明显**。

## 最佳实践

既然路径匹配器有两种方案，那必然有最佳实践。Spring官方对此也是持有态度的：

### Web环境

如果是Servlet应用（webmvc），官方推荐PathPattern（只是推荐，但默认的依旧是AntPathMatcher哈），相关代码体现在`PathPattern`里：

```java
// Since: 07.04.2003  
public abstract class AbstractHandlerMapping ... {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();  
 private PathMatcher pathMatcher = new AntPathMatcher();

  ...

  @Nullable  
 private PathPatternParser patternParser;  
 // Since: 5.3  
 public void setPatternParser(PathPatternParser patternParser) {  
  this.patternParser = patternParser;  
 }  
}

```

注意：`setPatternParser()`从5.3版本开始才被加入，也就说虽然PathPattern从Spring 5就有了，但直到5.3版本才被加入到webmvc里，且作为可选（默认依旧是AntPathMatcher）。换句话讲：在Spring 5.3版本之前，仍旧只能用AntPathMatcher。

#### 在WebMvc里启用PathPattern

默认情况下，Spring MVC依旧是使用的AntPathMatcher进行路径匹配的，那如何启用效率更高的PathPattern呢？

通过上面源码知道，就是要调用AbstractHandlerMapping的setPatternParser方法嘛，其实Spring为此是预留了扩展点的，只需这么做即可：

```java
/**  
 * 在此处添加备注信息  
 *  
 * @author YourBatman. <a href=mailto:yourbatman@aliyun.com>Send email to me</a>  
 * @site https://yourbatman.cn  
 * @date 2021/6/20 18:33  
 * @since 0.0.1  
 */  
@Configuration(proxyBeanMethods = false)  
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override  
    public void configurePathMatch(PathMatchConfigurer configurer) {  
        configurer.setPatternParser(PathPatternParser.defaultInstance);  
    }  
}

```

如果是Reactor应用（webflux），那PathPattern就是唯一解决方案。这体现在`org.springframework.web.reactive.handler.AbstractHandlerMapping`：

```java
// Since: 5.0  
public abstract class AbstractHandlerMapping... {

 private final PathPatternParser patternParser;  
 ...  
 public AbstractHandlerMapping() {  
  this.patternParser = new PathPatternParser();  
 }  
}

```

webflux里早已不见AntPathMatcher的踪影，因为webflux是从Spring 5.0开始的，因此没有向下兼容的负担，直接全面拥抱PathPattern了。

**结论：PathPattern语法更适合于web应用程序，其使用更方便且执行更高效。**

### 非Web环境

嗯，如果认真“听课”了的同学就知道：非Web环境依旧有且仅有一种选择，那便是AntPathMatcher，因为PathPattern是专为Web环境设计，不能用于非Web环境。所以像上面资源加载、包名扫描之类的，底层依旧是交给AntPathMatcher去完成。

> 说明：由于这类URL的解析绝大多数情况下匹配一次（执行一次）就行，所以微小的性能差异是无所谓的（对API来讲收益较大）

可能有小伙伴会说：在Service层，甚至Dao层我也可以正常使用PathPattern对象呀，何解？这个问题就相当于：`HttpServletRequest`属于web层专用组件，但你依旧可以将其传到Service层，甚至Dao层供以使用，在编译、运行时不会报错。但你可深入思考下，这么做合适吗？

> 举个生活上的例子：马桶可以装在卫生间，也可以安装在卧室的床旁边，都能完成大小便功能，但你觉得这么做合适吗？

Java这门语言对访问权限的控制设计得还是很优秀的，很多隔离性的问题在编译器就能搞定。但有很多规范性做法是无法做到**强约束**的，只能依靠工程师自身水平。这就是经验，也是区别初级工程师和高级工程师的重要因素。

## 总结

技术的日新月异，体现在一个个像PathPattern这个更好的API上。

Spring 5早在**2017-09**就已发布，可能是由于它“设计得过于优秀”，即使大版本的发布也几乎保持100%向下兼容，使得一般开发者感受不到它的升级。但是，这对框架二次开发者并不可能完全透明，因为二次开发经常会用到其Low-Level的API，比如今天的主角PathPattern就算其中之一，所以说我们要**与时俱进**呀o(╥﹏╥)o！

Spring 5虽然新增了（更好的）PathPattern，但它不能完全替代掉AntPathMatcher，因为前者**专为web设计**，所以在web领域是可完全替代掉AntPathMatcher的。但在非web领域内，AntPathMatcher依旧不可替代。

## 推荐阅读

-   [AntPathMatcher路径匹配器，Ant风格的URL](https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247494662&idx=1&sn=a104a55d869d7a7016c0fbfa07caaa67&scene=21#wechat_redirect)
-   [2个周末，历时100+小时，YourBatman新版Blog正式上线](https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247494239&idx=1&sn=c0eba8e7e43ab3c3825153f237427b44&scene=21#wechat_redirect)
-   [IDEA跟Eclipse险些打一架。Maven：都住手，我来一统天下](https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247493739&idx=1&sn=0c8d4c00ec41862d033c93662cab554e&scene=21#wechat_redirect)

![图片](https://mmbiz.qpic.cn/mmbiz_gif/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBQoznS1Fto8kiaPRooKD6xia5xlapXtokUG3gVLicbKTgDyyOTWNohv3iaA/640?wx_fmt=gif&wxfrom=5&wx_lazy=1)


> 我是`YourBatman`：一个早在2013年就已毕业的[大龄程序员](https://mp.weixin.qq.com/s?__biz=MzI0MTUwOTgyOQ==&mid=2247483765&idx=1&sn=591d5e6503aed1af1eb5ed8a053aea7c&scene=21#wechat_redirect)。网瘾失足、清考、延期毕业、房产中介、送外卖、销售...是我不可抹灭的标签。
> 
> -   **2013.08-2014.07**在宁夏银川中介公司卖二手房1年，毕业后第1份工作
> -   **2014.07-2015.05**在荆州/武汉/北京，从事炸鸡排、卖保险、直销以及送外卖工作，这是第2,3,4,5份工作
> -   **2015.08**从事Java开发，闯过外包，呆过大厂！Java架构师、博客专家，Spring开源贡献者。喜欢写代码，有代码洁癖；重视基础，坚信底层基础决定上层建筑
> -   **现致力于**写纯粹技术专栏，虽很难，但走自己的路不哗众取宠。如果你也有共鸣也是码农，可加我好友一起交流学习（备注：java）![图片](https://mmbiz.qpic.cn/mmbiz_jpg/crPesQVeyKLZrFANvRZEuQYXI3a4IOibBxJxDfhg2kKf4z7o7jrg6UmVHZS8mCWOOjP0xvN0ryhd7rDjmUHZxtQ/640?wx_fmt=jpeg&wxfrom=5&wx_lazy=1&wx_co=1)
