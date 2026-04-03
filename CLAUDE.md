# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build
./gradlew assemble

# Unit tests (shared module — runs on JVM via androidHostTest)
./gradlew :shared:testAndroidHostTest

# All tests with aggregated report
./gradlew allTests

# iOS simulator tests
./gradlew iosSimulatorArm64Test

# Instrumentation tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Journey tests
./gradlew testJourneysTestDefaultDebugTestSuite

# Lint
./gradlew lintDebug
```

## Architecture

**Clean Architecture** across three layers in the `shared` module:

- **`domain/`** — pure Kotlin, no framework dependencies
  - `model/` — data classes (`Device`, `Assembly`, `Composition`, `Price`) and `DeviceType` enum
  - `repository/` — interfaces only (`DeviceRepository`, `CurrentCompositionRepository`, etc.)
  - `use_case/` — one class per action; operators return `Flow<AppResponse<T>>` or suspend for mutations

- **`data/`** — implementations of domain interfaces
  - Room DB (`AppDatabase`, `AssemblyDeviceDao`) shared across Android and iOS via KSP
  - Ktor HTTP client (`AssemblePcApi`) with platform-specific engines
  - DataStore for preferences
  - DTOs and converters are separate from domain models

- **`presentation/`** — Compose Multiplatform screens and ViewModels
  - One ViewModel per screen, extending `BaseViewModel` (which extends `androidx.lifecycle.ViewModel`)
  - UI state modeled as sealed interfaces (e.g., `AppUiState.NoSelected`, `AppUiState.Selected`)
  - StateFlow for state, SharedFlow for one-time events
  - Navigation via AndroidX Navigation3 with serializable route objects

**DI:** Koin v4. Modules defined in `shared/src/commonMain/.../di/KoinModule.kt` — repositories and use cases as singletons, ViewModels via `koinViewModel()`.

**`AppResponse<T>`** sealed class (`Success`, `Loading`, `Failure`) wraps all async results from use cases.

## Module Layout

```
shared/
  src/
    commonMain/   ← domain + data + presentation (shared across platforms)
    commonTest/   ← unit tests + fixtures (Fake repositories, createDevice/createAssembly builders)
    androidMain/  ← Android-specific implementations (Room driver, Ktor OkHttp engine)
    iosMain/      ← iOS-specific implementations (Ktor Darwin engine, SQLite linking)
app/              ← Android application (Compose Activity, Firebase, Koin init)
iosApp/           ← Xcode project wrapping the shared KMP framework
```

## Test Conventions

Tests live in `shared/src/commonTest/kotlin/.../` mirroring the main source tree.

**Fixtures** (`fixtures/`):
- `FakeDeviceRepository` / `FakeCurrentCompositionRepository` — fake implementations with persistent `MutableStateFlow` per key; mutating methods (insert/delete/rename) update their flows
- `TestFixtures.kt` — builder functions `createDevice(...)` and `createAssembly(...)`

**Patterns:**
- Use `runTest {}` for coroutine tests
- Test names describe behavior: `` `invoke deletes specified quantity of assemblies` ``
- Assert via `fakeRepo.insertedAssemblies`, `fakeRepo.deletedAssemblies`, etc. (call records on fakes)
- `DeviceType` key-mapping tests must assert `cases.keys == DeviceType.entries.map { it.key }.toSet()` to stay exhaustive without hardcoding a count

## Key Conventions

- Use case classes are named `VerbNounUseCase`; repository interfaces are implemented as `InterfaceNameImpl`
- `DeviceType` enum has a custom serializer; always use `DeviceType.from(key)` for deserialization
- `Price` is a value-class wrapper — use its operators and formatters rather than raw numbers
- Platform-specific `expect`/`actual` declarations are in `androidMain` / `iosMain`
