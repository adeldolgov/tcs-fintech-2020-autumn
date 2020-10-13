package com.adeldolgov.homework_2.api

import com.adeldolgov.homework_2.data.pojo.*

class DataGenerator {
    fun getGroups(): Array<Source> {
        return arrayOf(
            Source(1, "page",
                "RADIO TAPOK",
                "Я Rоцкер, видеоблогер, пою, снимаю и всякое такое. #RADIOTAPOK",
                "https://sun1-95.userapi.com/c837630/v837630214/592fa/bNfalmrdOiE.jpg?ava=1"),
            Source(2, "group",
                "Comic Con Russia",
                "В этом году Comic Con Russia пройдет в онлайн-формате. С 3 по 4 октября в нашем сообществе мы будем вести прямую трансляцию из студии.",
                "https://sun1-29.userapi.com/c638427/v638427595/390a0/RfkHIZElifE.jpg?ava=1"),
            Source(3, "page",
                "Tinkoff Образование",
                "Описание",
                "https://sun9-10.userapi.com/WNVj9LgPVXfSmhwVV2vUsZYU7m5EPEZ0x9Eykg/FXVeAo8USz0.jpg"
            )
        )
    }

    fun getPosts(): Array<Post> {
        return arrayOf(
            Post(5, 1601926962, false, 1,
                "Тестовая запись без изображения!",
                null,
                Count(124),
                Count(1),
                Count(17)
            ),
            Post(4, 1599215502, false, 1,
                "Когда надоело хранить терабайты исходников, и ты решаешь сливать их на ютуб!" +
                        "\nВсем привет, я оператор, монтажер и колорист проекта #radiotapok." +
                        " И я запускаю новую историю, которая непосредственно касается нашего проекта." +
                        "\n\\\"Исходники\\\".\nЭто типа старого-доброго бекстейджа, но как по мне, атмосфернее и интереснее." +
                        "\n\nМузыка, cъемка, монтаж: SOLDATOV",
                arrayOf(Attachment(Photo(arrayOf("https://sun9-52.userapi.com/aChiei0UrZJKZP3iPGiINKYVQmZ5yWvRTlbNqg/Oe_TizNS5Dw.jpg")))),
                Count(751),
                Count(47),
                Count(17)
            ),
            Post(1, 1594981311, false, 3,
                "Занятия проходят по вторникам в Zoom. По некоторым лекциям предусмотрены семинары, которые проходят по четвергам.\n" +
                        "\nНачало занятий в 18.00, продолжительность примерно 1.5-2 часа." +
                        " Об изменениях в расписании и дополнительных занятиях будем сообщать заранее здесь и в чате.",
                arrayOf(Attachment(Photo(arrayOf("https://sun9-42.userapi.com/3qW0PZDoB8CFLD0lDsgNrQlflOaXQe0BdScYWg/5hiS6EqlfYg.jpg")))),
                Count(157),
                Count(15),
                Count(10)
            ),
            Post(2, 1439805951, false, 1,
                "Сегодня в 20:00 - подписываемся \nhttps://www.twitch.tv/radio_tapok \n" +
                        "К сожалению политика YouTube теперь не позволяет проводить обзоры на музыкальные клипы," +
                        " слишком частые баны стрима.",
                null,
                Count(1150),
                Count(10),
                Count(57)
            ),
            Post(3, 1439805951, false, 2,
                "#НовостиДрузей: день рождения Super Mario!\n\nSuper Mario отмечает свой 30-летний юбилей в этом году!" +
                        " 12 и 13 сентября в ЦДХ пройдет выставка в честь этого события! И мы тоже будем там!",
                arrayOf(Attachment(Photo(arrayOf("https://pp.userapi.com/c627731/v627731855/16638/-djrEjLZQSU.jpg")))),
                Count(10021),
                Count(350),
                Count(57)
            )
        )
    }

}