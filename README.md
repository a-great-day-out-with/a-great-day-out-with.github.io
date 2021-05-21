# A Great Day Out With...

Curated learning journeys for technologies like [Apache Kafka](https://a-great-day-out-with.github.io/kafka.html).

## Philosophy

The idea is to give an overview of key resources, concepts, projects, and products of a specific field,
providing guidance to users new in this area.

A journey is not meant to list _all_ potential items in any given category, but focus on the most relevant and helpful ones for newcomers.
Per category, there should not be more than seven items. Instead, additional sub-categories can be introduced whenever it makes sense to list more items.
The contents of a journey is in flux, i.e. items may be added and removed over time.

Suggestions for additions are highly appreciated, but the final decision about inclusion is made by the core maintainers of a journey.

## Build

* Load _kafka.excalidraw_ into [excalidraw.com](https://excalidraw.com), export as SVG to _kafka.svg_.
* Add/adjust links in _kafka-links.csv_
* Process the SVG file: `java Convert.java kafka`

Then open _docs/kafka.html_ in your browser.

## License

The drawings (*.excalidraw source files, *.png/*.svg published files) are  licensed under CC BY-SA 4.0.
All other source code in this repository is licensed under the Apache License Version 2.0.
