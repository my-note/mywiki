# GuavaSpliterJoiner

## 字符串切割Splitter

```java

List<String> bigbayAppIdList = Splitter.on(",")
  .omitEmptyStrings()
  .trimResults()
  .splitToList("123,fjdi, 88,99 , , 8 9 ")
  .forEach(System.out::println);

```


| 方法                                                  | 描述                                                   | 范例                                         |
|-------------------------------------------------------|--------------------------------------------------------|----------------------------------------------|
| Splitter.on(char)                                     | 按单个字符拆分                                         | Splitter.on(‘;’)                             |
| Splitter.on(CharMatcher)                              | 按字符匹配器拆分                                       | Splitter.on(CharMatcher.BREAKING_WHITESPACE) |
| Splitter.on(String)                                   | 按字符串拆分                                           | Splitter.on(",")                             |
| Splitter.on(Pattern)       Splitter.onPattern(String) | 按正则表达式拆分                                       | Splitter.onPattern("?")                      |
| Splitter.fixedLength(int)                             | 按固定长度拆分；最后一段可能比给定长度短，但不会为空。 | Splitter.fixedLength(3)                      |

## 拆分器修饰符

| 方法                     | 描述                                                   |
|--------------------------|--------------------------------------------------------|
| omitEmptyStrings()       | 从结果中自动忽略空字符串                               |
| trimResults()            | 移除结果字符串的前导空白和尾部空白                     |
| trimResults(CharMatcher) | 给定匹配器，移除结果字符串的前导匹配字符和尾部匹配字符 |
| limit(int)               | 限制拆分出的字符串数量                                 |


































