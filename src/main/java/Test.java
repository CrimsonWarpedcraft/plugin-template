import io.papermc.lib.PaperLib;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Created by Levi Muniz on 7/29/20.
 * Copyright (c) Levi Muniz. All Rights Reserved.
 */
public class Test extends JavaPlugin {
  static Logger logger;


  @Override
  public void onEnable() {
    logger = getLogger();

    PaperLib.suggestPaper(this);
  }
}
