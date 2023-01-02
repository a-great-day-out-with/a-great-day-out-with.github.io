# A Great Day Out With ...

Curated learning journeys for technologies like:
- [Apache Kafka](https://a-great-day-out-with.github.io/kafka.html)
- TODO: your learning journeys for XYZ ;-)

## Philosophy

The idea is to give an overview of key resources, concepts, projects, and products of a specific field,
providing guidance to users new in this area.

A journey is not meant to list _all_ potential items in any given category, but focus on the most relevant and helpful ones for newcomers.
Per category, there should not be more than seven items. Instead, additional sub-categories can be introduced whenever it makes sense to list more items.
The contents of a journey is in flux, i.e. items may be added and removed over time.

Suggestions for additions are highly appreciated, but the final decision about inclusion is made by the core maintainers of a journey.

## How does it work?

All journey specific artefacts are supposed to live in their own separate folders, see e.g. the `kafka` folder.
The following file naming conventions are important:

- `illustration.exaclidraw` **[mandatory]**: The main artefact for drawing and artwork for a journey and can be created from scratch or loaded using [excalidraw.com](https://excalidraw.com)
- `exported.svg` **[mandatory]**: The SVG version of the main artefact created by manually exporting your illustration file above from the excalidraw web UI
- `links.csv` **[mandatory]**: This file stores all URLs behind each textual element in the learning journey. Matching of text elements (drawing) and links (CSV file) is done by simple string replacements so make sure to get your names spelled correctly in both, the excalidraw and CSV file.
- `config.properties` _[optional]_: This file allows to configure specific "post-processing" actions for generating the output artefacts. Currently the following three properties - all of which are optional themselves - can be defined and are picked up by the `Converter.java`

    ```properties
    emojis.to.mirror=🚂;🚡;🚴‍♂️;⛵
    twitter.handles=@gunnarmorling;@hpgrahsl
    journey.short.url=https://bit.ly/journey4kafka
    ```

    - `emojis.to.mirror`: allows to define a list of emojis which are used in the drawing and should get mirrored horizontally if needed
    - `twitter.handles`: in case author information is given by textual twitter handles, those handles can be replaced with links to profile URLs if needed
    - `journey.short.url`: if applicable a textual tiny URL referring to the project repository can be replaced with the corresponding HTML link


## How to contribute to an existing journey

### The pretty simple way:

1. add/modify links in the `<journey_folder>/links.csv` file
2. create a PR for this change

_NOTE: The core maintainers will do their best to make all changes to graphical artefacts (excalidraw + SVG) that are most likely needed due to changes in the `links.csv` file._

### The more involved way:

1. add/modify links in the `<journey_folder>/links.csv` file
2. load `<journey_folder>/illustration.excalidraw` into [excalidraw.com](https://excalidraw.com), make graphical changes if needed and export an SVG file into `<journey_folder>/exported.svg`
3. run java converter tool `java Convert.java <journey>` which processes the `<journey_folder>/exported.svg` and writes the final result into `docs/<journey_folder>/generated.svg`
4. open `docs/<journey_folder>/index.html` in your browser to verify if your changes are properly  reflected in the generated SVG file that's embedded in this HTML document.
5. create a PR for your change which should usually contain 4 changed files, namely: `<journey_folder>/links.csv`, `<journey_folder>/illustration.excalidraw`, `<journey_folder>/exported.svg`, `docs/<journey_folder>/generated.svg`

## License

The drawings (*.excalidraw source files, *.png/*.svg published files) are  licensed under CC BY-SA 4.0.
All other source code in this repository is licensed under the Apache License Version 2.0.
