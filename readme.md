Запуск контейнера через команды: 

docker-compose up --build -d
docker-compose logs -f route-app

далее выполнения запроса в postman по адресу:
http://localhost:8080/api/routes/generate-ids

Шаблон запроса в Postman прикреплю сюда:

    /*
    {
        "prompt": "Я хочу выпить кофе в течение двух часов и посмотреть парки",
        "duration_hours": 2,
        "points": [
            {
                "id": "1",
                "name": "Эрмитаж",
                "category": "Образование",
                "avg_visit_duration_min": 600
            },
            {
                "id": "2",
                "name": "Центральный Музей",
                "category": "Образование",
                "avg_visit_duration_min": 240
            },
            {
                "id": "3",
                "name": "Skuratov Coffee",
                "category": "Еда",
                "avg_visit_duration_min": 30
            },
            {
                "id": "4",
                "name": "Галерея Современного Искусства",
                "category": "Образование",
                "avg_visit_duration_min": 120
            },
            {
                "id": "5",
                "name": "Парк Победы",
                "category": "Развлечение",
                "avg_visit_duration_min": 60
            }
        ]
    }
    */

Шаблон для проверки массива из 100 точек:
{
  "prompt": "Я хочу выпить кофе в течение двух часов и посмотреть парки",
  "duration_hours": 2,
  "points": [
    {
      "id": "1",
      "name": "Центральный парк культуры",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "2",
      "name": "Кофейня 'У озера'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "3",
      "name": "Ботанический сад",
      "category": "Парк",
      "avg_visit_duration_min": 60
    },
    {
      "id": "4",
      "name": "Starbucks на набережной",
      "category": "Кофейня",
      "avg_visit_duration_min": 20
    },
    {
      "id": "5",
      "name": "Парк Победы",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "6",
      "name": "Coffeeline",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "7",
      "name": "Детский парк развлечений",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "8",
      "name": "Кофейня 'У фонтана'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "9",
      "name": "Японский сад",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "10",
      "name": "Costa Coffee",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "11",
      "name": "Парк 300-летия",
      "category": "Парк",
      "avg_visit_duration_min": 70
    },
    {
      "id": "12",
      "name": "Кофейня 'В тени деревьев'",
      "category": "Кофейня",
      "avg_visit_duration_min": 40
    },
    {
      "id": "13",
      "name": "Летний сад",
      "category": "Парк",
      "avg_visit_duration_min": 65
    },
    {
      "id": "14",
      "name": "DoubleB Coffee",
      "category": "Кофейня",
      "avg_visit_duration_min": 20
    },
    {
      "id": "15",
      "name": "Парк аттракционов",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "16",
      "name": "Кофейня 'У пруда'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "17",
      "name": "ЦПКиО",
      "category": "Парк",
      "avg_visit_duration_min": 80
    },
    {
      "id": "18",
      "name": "Coffee House",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "19",
      "name": "Английский парк",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "20",
      "name": "Кофейня 'Панорама'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "21",
      "name": "Парк скульптур",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "22",
      "name": "Кофейня 'У реки'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "23",
      "name": "Природный заповедник",
      "category": "Парк",
      "avg_visit_duration_min": 90
    },
    {
      "id": "24",
      "name": "Coffee Room",
      "category": "Кофейня",
      "avg_visit_duration_min": 20
    },
    {
      "id": "25",
      "name": "Городской сад",
      "category": "Парк",
      "avg_visit_duration_min": 35
    },
    {
      "id": "26",
      "name": "Кофейня 'В парке'",
      "category": "Кофейня",
      "avg_visit_duration_min": 40
    },
    {
      "id": "27",
      "name": "Парк птиц",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "28",
      "name": "Paul Bakery",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "29",
      "name": "Дендрарий",
      "category": "Парк",
      "avg_visit_duration_min": 60
    },
    {
      "id": "30",
      "name": "Кофейня 'Садовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "31",
      "name": "Парк флоры",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "32",
      "name": "Кофейня 'Зеленая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "33",
      "name": "Парк у озера",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "34",
      "name": "Surf Coffee",
      "category": "Кофейня",
      "avg_visit_duration_min": 20
    },
    {
      "id": "35",
      "name": "Парк развлечений",
      "category": "Парк",
      "avg_visit_duration_min": 75
    },
    {
      "id": "36",
      "name": "Кофейня 'У дороги'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "37",
      "name": "Исторический парк",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "38",
      "name": "Кофейня 'Тропикана'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "39",
      "name": "Парк у моря",
      "category": "Парк",
      "avg_visit_duration_min": 65
    },
    {
      "id": "40",
      "name": "Кофейня 'Ветерок'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "41",
      "name": "Парк культуры",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "42",
      "name": "Кофейня 'Солнечная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "43",
      "name": "Парк отдыха",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "44",
      "name": "Кофейня 'Лесная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 40
    },
    {
      "id": "45",
      "name": "Парк сказок",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "46",
      "name": "Кофейня 'Радуга'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "47",
      "name": "Парк животных",
      "category": "Парк",
      "avg_visit_duration_min": 60
    },
    {
      "id": "48",
      "name": "Кофейня 'Оазис'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "49",
      "name": "Парк раздумий",
      "category": "Парк",
      "avg_visit_duration_min": 35
    },
    {
      "id": "50",
      "name": "Кофейня 'Тишина'",
      "category": "Кофейня",
      "avg_visit_duration_min": 40
    },
    {
      "id": "51",
      "name": "Парк фонтанов",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "52",
      "name": "Кофейня 'Уютная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "53",
      "name": "Парк мостов",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "54",
      "name": "Кофейня 'Времена года'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "55",
      "name": "Парк аллей",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "56",
      "name": "Кофейня 'Ароматная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "57",
      "name": "Парк холмов",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "58",
      "name": "Кофейня 'Высокая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 20
    },
    {
      "id": "59",
      "name": "Парк долин",
      "category": "Парк",
      "avg_visit_duration_min": 65
    },
    {
      "id": "60",
      "name": "Кофейня 'Низкая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "61",
      "name": "Парк ручьев",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "62",
      "name": "Кофейня 'Быстрая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "63",
      "name": "Парк водопадов",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "64",
      "name": "Кофейня 'Медленная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 40
    },
    {
      "id": "65",
      "name": "Парк родников",
      "category": "Парк",
      "avg_visit_duration_min": 35
    },
    {
      "id": "66",
      "name": "Кофейня 'Свежая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "67",
      "name": "Парк ив",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "68",
      "name": "Кофейня 'Старая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "69",
      "name": "Парк берез",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "70",
      "name": "Кофейня 'Новая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "71",
      "name": "Парк дубов",
      "category": "Парк",
      "avg_visit_duration_min": 60
    },
    {
      "id": "72",
      "name": "Кофейня 'Дубовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "73",
      "name": "Парк кленов",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "74",
      "name": "Кофейня 'Кленовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "75",
      "name": "Парк сосен",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "76",
      "name": "Кофейня 'Сосновая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "77",
      "name": "Парк елей",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "78",
      "name": "Кофейня 'Еловая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "79",
      "name": "Парк лип",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "80",
      "name": "Кофейня 'Липовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "81",
      "name": "Парк каштанов",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "82",
      "name": "Кофейня 'Каштановая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "83",
      "name": "Парк тополей",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "84",
      "name": "Кофейня 'Тополиная'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "85",
      "name": "Парк ивняка",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "86",
      "name": "Кофейня 'Ивовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "87",
      "name": "Парк ольхи",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "88",
      "name": "Кофейня 'Ольховая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "89",
      "name": "Парк ясеней",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "90",
      "name": "Кофейня 'Ясеневая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "91",
      "name": "Парк вязов",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "92",
      "name": "Кофейня 'Вязовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "93",
      "name": "Парк акаций",
      "category": "Парк",
      "avg_visit_duration_min": 45
    },
    {
      "id": "94",
      "name": "Кофейня 'Акациевая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    },
    {
      "id": "95",
      "name": "Парк сирени",
      "category": "Парк",
      "avg_visit_duration_min": 50
    },
    {
      "id": "96",
      "name": "Кофейня 'Сиреневая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 30
    },
    {
      "id": "97",
      "name": "Парк жасмина",
      "category": "Парк",
      "avg_visit_duration_min": 40
    },
    {
      "id": "98",
      "name": "Кофейня 'Жасминовая'",
      "category": "Кофейня",
      "avg_visit_duration_min": 25
    },
    {
      "id": "99",
      "name": "Парк магнолий",
      "category": "Парк",
      "avg_visit_duration_min": 55
    },
    {
      "id": "100",
      "name": "Кофейня 'Магнолия'",
      "category": "Кофейня",
      "avg_visit_duration_min": 35
    }
  ]
}
