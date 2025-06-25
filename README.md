# Factorio Blueprint String Renderer (FBSR)

A Java application that renders Factorio blueprint strings into images. This project can operate as a standalone web API service, Discord bot, Reddit bot, or be integrated into other Java applications.

## Features

- 🖼️ **Blueprint Rendering**: Convert Factorio blueprint strings to high-quality PNG images
- 🌐 **Web API**: RESTful API for blueprint rendering services
- 🤖 **Bot Integration**: Discord and Reddit bot support
- 🏭 **Mod Support**: Supports vanilla and modded Factorio content
- 📊 **Multiple Formats**: Support for individual blueprints and blueprint books
- ⚙️ **Configurable**: Flexible configuration for different deployment scenarios

## Quick Start

### Prerequisites

- Java 11 or higher
- Eclipse IDE for Java Developers (recommended)
- Git

### Installation with Eclipse

#### Prerequisites Setup
Before starting, you'll need either:
- **Option A**: A full Factorio installation (Steam, GOG, or standalone) for generating data files
- **Option B**: Pre-generated data files from the Java-Factorio-Data-Wrapper repository

#### Installation Steps

1. **Clone the required repositories:**
   ```bash
   git clone https://github.com/demodude4u/Factorio-FBSR.git
   git clone https://github.com/demodude4u/Java-Factorio-Data-Wrapper.git
   git clone https://github.com/demodude4u/Discord-Core-Bot-Apple.git
   ```

2. **Import projects into Eclipse:**
   - Open Eclipse
   - File → Import → General → Existing Projects into Workspace
   - Select root directory and browse to where you cloned the repositories
   - Import all three projects:
     - `FactorioBlueprintStringRenderer`
     - `FactorioDataWrapper` 
     - `DiscordCoreBotApple`

3. **Configure the project:**
   - Choose the appropriate configuration file:
     - For **development/building**: Copy `config.build.json` to `config.json`
     - For **runtime only**: Copy `config.run.json` to `config.json`
   - Edit `config.json` with your settings (see Configuration section below)

4. **Set up dependencies:**
   - The projects use Maven for dependency management
   - Eclipse should automatically download dependencies
   - If you encounter issues, right-click each project → Maven → Reload Projects

5. **Create required directories:**
   ```bash
   mkdir -p FactorioBlueprintStringRenderer/mods/mods-vanilla
   mkdir -p FactorioBlueprintStringRenderer/data/mods-vanilla/script-output
   ```

6. **Add required files:**
   - Copy `mod-rendering.json` from `res/` to `mods/mods-vanilla/`

7. **Generate data files (if using Factorio installation):**
   - Use `config.build.json` and configure Factorio paths
   - Run the application once to generate atlas and data files
   - Switch to `config.run.json` for subsequent runs

## Configuration

The project provides two configuration templates:

### Runtime Configuration (config.run.json)
Use this for production deployments or when you have pre-generated data dumps:

```json
{
    "webapi": {
        "bind": "0.0.0.0",
        "port": 8082,
        "use-local-storage": true,
        "local-storage": "./output"
    },
    "factorio_manager": {
        "mods": "mods",
        "data": "data"
    }
}
```

### Build Configuration (config.build.json)
Use this for development or when you need to generate data dumps from a Factorio installation:

```json
{
    "webapi": {
        "bind": "0.0.0.0",
        "port": 8082,
        "use-local-storage": true,
        "local-storage": "./output"
    },
    "factorio_manager": {
        "install": "/path/to/factorio",
        "executable": "/path/to/factorio/bin/x64/factorio",
        "mods": "mods",
        "data": "data"
    }
}
```

### Configuration Options

#### Web API Settings
- `bind`: IP address to bind to (default: "0.0.0.0")
- `port`: Port number for the web service (default: 8082)
- `use-local-storage`: Save images locally instead of using Discord hosting
- `local-storage`: Directory path for storing generated images

#### Factorio Manager Settings
- `mods`: Directory containing mod files
- `data`: Directory containing Factorio data dumps
- `install`: (Build config only) Path to Factorio installation
- `executable`: (Build config only) Path to Factorio executable

## Web API Usage

### Starting the Web API

Run the main class: `com.demod.fbsr.app.StartAllServices`

The API will be available at `http://localhost:8082` (or your configured host/port).

### API Endpoints

#### POST /blueprint

Renders a Factorio blueprint string to an image.

**Request Body:**
```json
{
    "blueprint": "0eNqVkm1uwyAMhq...",
    "max-width": 1920,
    "max-height": 1080,
    "show-info-panels": false,
    "return-single-image": true
}
```

**Parameters:**
- `blueprint` (required): The Factorio blueprint string
- `max-width` (optional): Maximum image width in pixels
- `max-height` (optional): Maximum image height in pixels  
- `show-info-panels` (optional): Whether to show information panels
- `return-single-image` (optional): Return PNG image directly instead of JSON

**Response (JSON mode):**
```json
{
    "info": ["Success message"],
    "images": [
        {
            "label": "Blueprint Name",
            "link": "path/to/image.png"
        }
    ]
}
```

**Response (Single Image mode):**
Returns PNG image data directly with `Content-Type: image/png`

### Example Usage

#### cURL Example
```bash
curl -X POST http://localhost:8082/blueprint \
  -H "Content-Type: application/json" \
  -d '{
    "blueprint": "0eNqVkm1uwyAMhq8S...",
    "return-single-image": true
  }' \
  --output blueprint.png
```

#### Python Example
```python
import requests
import json

url = "http://localhost:8082/blueprint"
data = {
    "blueprint": "0eNqVkm1uwyAMhq8S...",
    "max-width": 1920,
    "max-height": 1080
}

response = requests.post(url, json=data)
result = response.json()
print(f"Generated images: {result['images']}")
```

#### JavaScript Example
```javascript
const response = await fetch('http://localhost:8082/blueprint', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        blueprint: '0eNqVkm1uwyAMhq8S...',
        'return-single-image': true
    })
});

if (response.ok) {
    const blob = await response.blob();
    // Use blob as image data
} else {
    console.error('Failed to render blueprint');
}
```

## Running in Eclipse

1. **Set up run configuration:**
   - Right-click on `FactorioBlueprintStringRenderer` project
   - Run As → Java Application
   - Select `StartAllServices` as the main class

2. **Monitor the console:**
   - Check for "ALL SERVICES ARE HEALTHY!" message
   - Web API will be available once started

3. **Testing:**
   - Use the API endpoints described above
   - Check the console for request logs

## Project Dependencies

This project requires two additional repositories to function properly:

### [Java-Factorio-Data-Wrapper](https://github.com/demodude4u/Java-Factorio-Data-Wrapper)
- Handles Factorio game data extraction and processing
- Provides data models for items, recipes, entities, etc.
- Required for blueprint parsing and rendering

### [Discord-Core-Bot-Apple](https://github.com/demodude4u/Discord-Core-Bot-Apple)
- Discord bot framework (if using Discord integration)
- Provides command handling and Discord API integration
- Optional unless using Discord bot features

## Data Requirements

### Factorio Installation (Initial Setup Only)

**Important**: You need a **full Factorio installation** for the initial setup to generate required data files. Once these files are generated, the Factorio installation is no longer needed for runtime operation.

#### What Gets Generated:
1. **Atlas files** - Sprite sheets containing all game graphics
2. **data-raw-dump.zip** - Complete game data in JSON format
3. **Mod data** - Information about available mods and entities

#### Initial Setup Process:

1. **Install Factorio** (if not already installed)
   - Steam, GOG, or standalone version all work
   - Must be a complete installation with the `data/` directory

2. **Generate Data Files** (One-time setup)
   - Use `config.build.json` as your base configuration
   - Configure `factorio_manager.install` to point to your Factorio installation
   - Configure `factorio_manager.executable` to point to the Factorio executable
   - Run the application - it will generate all required data files

3. **Switch to Runtime Mode** (For production)
   - Copy `config.run.json` to `config.json` 
   - The application now runs standalone without needing Factorio installed
   - Generated data files are reused for all blueprint rendering

#### Alternative: Use Pre-generated Data
If you don't want to install Factorio, you can:
- Download pre-generated data dumps from the Java-Factorio-Data-Wrapper repository
- Place `data-raw-dump.zip` in `data/mods-vanilla/script-output/`
- Use `config.run.json` directly

### Required Files Structure
```
FactorioBlueprintStringRenderer/
├── config.json
├── mods/
│   └── mods-vanilla/
│       ├── mod-list.json
│       └── mod-rendering.json
└── data/
    └── mods-vanilla/
        └── script-output/
            └── data-raw-dump.zip
```

## Troubleshooting

### Common Issues

#### "JSONObject["factorio"] not found"
- Use `config.run.json` instead of `config.build.json` 
- Ensure you have a valid `data-raw-dump.zip` file in the data directory

#### "NullPointerException: source"
- Check that all required resource files are included in your JAR
- Verify the `res/` directory contains all JSON configuration files

#### "Cannot run program" errors
- Factorio installation path is incorrect or Factorio is not installed
- Use pre-generated data dumps instead of live generation

#### Port already in use
- Change the `port` setting in the `webapi` configuration
- Check if another service is using the configured port

### Debug Mode

Enable debug logging by adding to your config.json:
```json
{
    "logging": {
        "file": "debug.log",
        "level": "DEBUG"
    }
}
```

## Deployment Scenarios

### Development/Initial Setup
1. Install Factorio on your development machine
2. Use `config.build.json` to generate data files
3. Run locally for testing and development

### Production Deployment
1. Copy generated data files (`data/` and `mods/` directories) to production server
2. Use `config.run.json` - no Factorio installation needed on production server
3. Deploy as JAR or Docker container

### Team/CI Setup
1. One team member or CI system generates data files using Factorio installation
2. Commit generated data files to version control or artifact storage
3. All other environments use `config.run.json` with pre-generated data

## Building for Production

### Creating a Runnable JAR

1. In Eclipse: Right-click project → Export → Java → Runnable JAR file
2. Choose "Extract required libraries into generated JAR"
3. Select `StartAllServices` as the launch configuration
4. Export to your desired location

### Docker Deployment

Create a `Dockerfile`:
```dockerfile
FROM openjdk:11-jre-slim

COPY fbsr.jar /app/fbsr.jar
COPY config.json /app/config.json
COPY mods/ /app/mods/
COPY data/ /app/data/

WORKDIR /app
EXPOSE 8082

CMD ["java", "-jar", "fbsr.jar"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Related Projects

- [Java-Factorio-Data-Wrapper](https://github.com/demodude4u/Java-Factorio-Data-Wrapper) - Factorio data extraction library
- [Discord-Core-Bot-Apple](https://github.com/demodude4u/Discord-Core-Bot-Apple) - Discord bot framework
- [Factorio](https://factorio.com/) - The game this project supports

## Support

- Create an issue on GitHub for bugs or feature requests
- Check the existing issues for known problems and solutions
- Refer to the Factorio modding documentation for blueprint format details 