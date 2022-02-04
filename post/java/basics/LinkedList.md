# LinkedList

* 双向列表
* 增删快、查找慢、线程不安全
    - 查询效率低（不支持随机访问），要查询第n个元素，必须从第一个元素开始，逐一向后遍历，直到第n个元素
* 新增元素：尾插法（多线程下可能引发数据丢失）
* ArrayList vs LinkedList
    - ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
    - 对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
    - 对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
* 他还提供了List接口中没有定义的方法，专门用于操作表头和表尾元素，可以当作堆栈、队列和双向队列使用。


















