package org.m450.budgetplanner.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class JsonStorageService {

  private final Gson gson = new GsonBuilder()
          .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
          .setPrettyPrinting()
          .create();

  private final Path filePath;

  public JsonStorageService(String filePath) {
    this.filePath = Path.of(filePath);
  }

  public AppData load() {
    if (!Files.exists(filePath)) {
      return new AppData();
    }
    try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
      AppData data = gson.fromJson(reader, AppData.class);
      return data != null ? data : new AppData();
    } catch (IOException e) {
      throw new RuntimeException("Could not read data file: " + filePath, e);
    }
  }

  public void save(AppData data) {
    if (data == null) {
      throw new IllegalArgumentException("AppData must not be null");
    }
    try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
      gson.toJson(data, writer);
    } catch (IOException e) {
      throw new RuntimeException("Could not write data file: " + filePath, e);
    }
  }
}