# TreeSet

* TreeSet是`SortedSet<<inter>>`的唯一实现类
* 支持排序（自然排序、指定排序）
* 自然排序是默认排序
* 二叉树实现,平衡二叉树更具体一点是红黑树
* 不允许放入null
* 不支持相同元素（去重），判断元素是否相同是通过equals是否返回0


## 使用场景：根据对象的属性去重

下面要根据Person对象的id去重，那该怎么做呢？

写一个方法吧:

```java
public class Person {
    private Long id;

    private String name;
}
```


```java
public static List<Person> removeDupliById(List<Person> persons) {
   Set<Person> personSet = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
   personSet.addAll(persons);

   return new ArrayList<>(personSet);
}
```
再来看比较炫酷的Java8写法:

```java
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

// 根据id去重
List<Person> unique = persons.stream().collect( 
    collectingAndThen(
        toCollection(() -> new TreeSet<>(comparingLong(Person::getId))), 
        ArrayList::new
    )
);
```























































