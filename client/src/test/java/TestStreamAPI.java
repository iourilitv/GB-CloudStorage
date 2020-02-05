import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestStreamAPI {
    private static class Person {
        enum Position {
            ENGINEER, DIRECTOR, MANAGER
        }

        private String name;
        private int age;
        private Position position;

        public Person(String name, int age, Position position) {
            this.name = name;
            this.age = age;
            this.position = position;
        }
    }

    public static void main(String[] args) {
//        myOwnFilterEx();
//        countExample();
//        simpleStringEx();
        streamFromFilesEx();
    }

    private static void flatMapEx() {
        try {
            Files.lines(Paths.get("text.txt"))
                    .map(line -> line.split("\\s"))
                    .distinct()
                    .forEach(arr -> System.out.println(Arrays.toString(arr)));
            System.out.println("----------------------");
            Files.lines(Paths.get("text.txt"))
                    .map(line -> line.split("\\s")) // arr[0] arr[1] arr[2] arr[3]
                    .map(Arrays::stream)
                    .distinct()
                    .forEach(System.out::println);
            System.out.println("----------------------");
            System.out.println(Files.lines(Paths.get("text.txt"))
                    .map(line -> line.split("\\s")) // arr[0] arr[1] arr[2] arr[3]
                    .flatMap(Arrays::stream)
                    .distinct()
                    .collect(Collectors.joining(", ", "Уникальные слова: ", ".")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileToString() throws IOException {
        return Files.lines(Paths.get("text.txt")).
                collect(Collectors.joining("\n"));
    }

    private static void parallelStreamEx() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
        list.parallelStream().filter(i -> {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            return true;
        }).collect(Collectors.toSet());
    }

    private static void simpleStringEx() {
        System.out.println(Arrays.stream("A B CC B C AA A A B CC C".
                split("\\s")).distinct().count());
        //5
        System.out.println(Arrays.toString(Arrays.stream("A B CC B C AA A A B CC C".
                split("\\s")).distinct().toArray()));
        //[A, B, CC, C, AA]
    }

    private static void streamFromFilesEx() {
        try {
//            Files.lines(Paths.get("123.txt")).map(String::length).
//                    forEach(System.out::println);////java.nio.file.NoSuchFileException: 123.txt

            Files.lines(Paths.get(ClassLoader.getSystemResource("123.txt").toURI()))
                    .map(String::length)
                    .forEach(System.out::println);
            //26
            //20

//            Files.lines(Paths.get("D:\\GeekBrains\\20191130_GB-Разработка_сетевого_хранилища_на_Java\\cloudstorage\\client\\target\\test-classes\\123.txt")).map(String::length).
//                    forEach(System.out::println);


        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void streamCreationEx() {
        Arrays.asList("A", "B", "C").stream().forEach(System.out::println);
        Stream.of(1, 2, 3, 4).forEach(System.out::println);
        Arrays.stream(new int[]{4, 3, 2, 1}).forEach(System.out::println);
    }

    private static void intStreamsEx() {
        IntStream myIntStream = IntStream.of(10, 20, 30, 40, 50);

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        list.stream().mapToInt(v -> v).sum();

        IntStream.rangeClosed(2, 10).filter(n -> n % 2 == 0).forEach(System.out::println);
    }

    private static void reduceEx() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        int sum = 0;
        for (Integer o : list) {
            sum += o;
        }

        int streamSum = list.stream().reduce(0, (a, b) -> a + b);
        System.out.println(sum + " " + streamSum);
    }

    private static void mappingEx() {
        Function<String, Integer> _strToLen = String::length;
        Function<String, Integer> strToLen = s -> s.length();
        Predicate<Integer> evenNumberFilter = n -> n % 2 == 0;
        Function<Integer, Integer> cube = n -> n * n * n;

        Stream.of(1, 2, 3).map(n -> Math.pow(n, 3));
        Stream.of(1, 2, 3).map(cube);

        List<String> list = Arrays.asList("A", "BB", "C", "DDD", "EE", "FFFF");
//        List<Integer> wordsLength = list.stream().map(str -> str.length()).collect(Collectors.toList());
        List<Integer> wordsLength = list.stream().map(String::length).collect(Collectors.toList());
//        List<Integer> wordsLength = list.stream().map(strToLen).collect(Collectors.toList());

        System.out.println(list);
        System.out.println(wordsLength);

        list.stream().map(strToLen).forEach(n -> System.out.println(n));
        list.stream().map(strToLen).forEach(System.out::println);
    }

    private static void findAnyEx() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        list.stream().filter(n -> n > 10).findAny().ifPresent(System.out::println);

        Optional<Integer> opt = list.stream().filter(n -> n > 10).findAny();
        if (opt.isPresent()) {
            System.out.println(opt.get());
        }
    }

    public static String returnOrThrow() {
        Optional<String> opt = Optional.of("Java");
        return opt.orElseThrow(() -> new RuntimeException());
    }

    public static void countExample() {
        Map<Integer, Long> map = Stream.of("A", "BB", "AA", "B", "C", "EE", "NNN", "X", "QQ").
                collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println(map);//{1=4, 2=4, 3=1}
    }

    private static void matchEx() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(list.stream().allMatch(n -> n < 10));
        System.out.println(list.stream().anyMatch(n -> n == 4));
        System.out.println(list.stream().noneMatch(n -> n == 2));
    }

    private static void thirdEx() {
        // получаем поток данных из набора целых чисел, находим среди них только уникальные,
        // и каждое найденное значение выводим в консоль
        System.out.println("Первый вариант: ");
        Arrays.asList(1, 2, 3, 4, 4, 3, 2, 3, 2, 1).stream().distinct().forEach(n -> System.out.println(n));
        // делаем то же самое, что и в первом случае, только используем более короткую запись System.out::println
        System.out.println("Второй вариант: ");
        Arrays.asList(1, 2, 3, 4, 4, 3, 2, 3, 2, 1).stream().distinct().forEach(System.out::println);
    }

    private static void secondEx() {
        // Создаем список целых чисел
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> out = numbers.stream()  // преобразуем список целых чисел в поток данных и начинаем обработку
                .filter(n -> n % 2 == 0)      // фильтруем поток, оставляем в нем только четные числа
                .map(n -> n * n)              // преобразуем каждый элемент потока int -> int, умножая на 2
                .limit(2)                     // оставляем в потоке только 2 первых элемента
                .collect(Collectors.toList());// собираем элементы потока в лист

        System.out.println(numbers);
        System.out.println(out);
    }

    private static void filterEx() {
        Stream.of(1, 2, 3, 4, 5, 6, 7, 8).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer % 2 == 0;
            }
        }).forEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println(integer);
            }
        });

        Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                .filter(n -> n % 2 == 0)
                .forEach(n -> System.out.println(n));
    }

    private static void firstEx() {
        List<Person> persons = new ArrayList<>(Arrays.asList(
                new Person("Bob1", 35, Person.Position.MANAGER),
                new Person("Bob2", 44, Person.Position.DIRECTOR),
                new Person("Bob3", 25, Person.Position.ENGINEER),
                new Person("Bob4", 42, Person.Position.ENGINEER),
                new Person("Bob5", 55, Person.Position.MANAGER),
                new Person("Bob6", 19, Person.Position.MANAGER),
                new Person("Bob7", 33, Person.Position.ENGINEER),
                new Person("Bob8", 37, Person.Position.MANAGER)
        ));

        List<Person> engineers = new ArrayList<>();
        for (Person o : persons) {
            if (o.position == Person.Position.ENGINEER) {
                engineers.add(o);
            }
        }
        engineers.sort(new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.age - o2.age;
            }
        });
        List<String> engineersNames = new ArrayList<>();
        for (Person o : engineers) {
            engineersNames.add(o.name);
        }
        System.out.println(engineersNames);

        List<String> engineersNamesShortPath = persons.stream()
                .filter(p -> p.position == Person.Position.ENGINEER)
                .sorted((o1, o2) -> o1.age - o2.age)
                .map(person -> person.name)
                .collect(Collectors.toList());

        System.out.println(engineersNamesShortPath);
    }

    public static void myOwnFilterEx() {
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        System.out.println(myOwnFilter(ints, i -> i % 2 == 0));//[2, 4, 6, 8]
        System.out.println(myOwnFilter(ints, i -> i % 2 != 0));//[1, 3, 5, 7]
    }

    /**
     * Метод удаляет из коллекции все элементы, не соотвествующие предикатору(условию).
     * @param list - заданная коллекция
     * @param predicate - заданный предикатор(условие)
     * @param <T> - тип элемента коллекции
     * @return - коллекцию, отфильтрованную по условию
     */
    public static <T> List<T> myOwnFilter(List<T> list, Predicate<T> predicate) {
        List<T> copy = new ArrayList<>(list);
        Iterator<T> iter = copy.iterator();
        while (iter.hasNext()) {
            T o = iter.next();
            if (!predicate.test(o)) {
                iter.remove();
            }
        }
        return copy;
    }
}
