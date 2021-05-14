package fr.politicraft.bag.config;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.util.TextAdapter;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class YmlMessage {

    private Main main;
    private FileConfiguration config;

    public YmlMessage(Main main) {
        this.main = main;
        this.config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/" + YmlFile.MESSAGES));
    }

    public String getPrefixMessage() {
        return TextAdapter.colorize(config.getString("message-prefix"));
    }

    public void send(Player player, String message) {
        player.sendMessage(getPrefixMessage() + message);
    }

    // Chat Items //

    public int getTimingToWriteAmount() {
        return config.getInt("time-write-amount");
    }

    public String getDepositRequestMessage() {
        return TextAdapter.colorize(config.getString("deposit-request"));
    }

    public String getWithdrawalRequestMessage() {
        return TextAdapter.colorize(config.getString("withdrawal-request"));
    }

    public String getDepositTransactionMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("deposit-transaction")), amount, null);
    }

    public String getDepositTransactionAdjustedMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("deposit-transaction-adjusted")), amount, null);
    }

    public String getDepositTransactionErrorMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("deposit-transaction-error")), amount, null);
    }

    public String getWithdrawalTransactionMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("withdrawal-transaction")), amount,null);
    }

    public String getWithdrawalTransactionAdjustedMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("withdrawal-transaction-adjusted")), amount, null);
    }

    public String getWithdrawalTransactionErrorMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("withdrawal-transaction-error")), amount,null);
    }

    // Chat XP //

    public String getExpDepositRequestMessage() {
        return TextAdapter.colorize(config.getString("exp-deposit-request"));
    }

    public String getExpWithdrawalRequestMessage() {
        return TextAdapter.colorize(config.getString("exp-withdrawal-request"));
    }


    public String getExpDepositTransactionMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-deposit-transaction")), amount, null);
    }

    public String getExpDepositTransactionAdjustedMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-deposit-transaction-adjusted")), amount, null);
    }

    public String getExpDepositTransactionErrorMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-deposit-transaction-error")), amount, null);
    }

    public String getExpWithdrawalTransactionMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-withdrawal-transaction")), amount, null);
    }

    public String getExpWithdrawalTransactionAdjustedMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-withdrawal-transaction-adjusted")), amount, null);
    }

    public String getExpWithdrawalTransactionErrorMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("exp-withdrawal-transaction-error")), amount, null);
    }

    // Chat General Errors //

    public String getInvalidNumberMessage() {
        return TextAdapter.colorize(config.getString("invalid-number"));
    }

    // Commands //

    public String getSortSummaryMessage() {
        return TextAdapter.colorize(config.getString("sort-summary"));
    }

    public String getSortSummaryHoverMessage() {
        return TextAdapter.colorize(config.getString("sort-summary-hover"));
    }

    public String getSortSummaryClickableWord() {
        return TextAdapter.colorize(config.getString("sort-summary-clickable-word"));
    }

    public String getUnknownPlayerMessage() {
        return TextAdapter.colorize(config.getString("unknown-player"));
    }

    public String getPermissionErrorMessage() {
        return TextAdapter.colorize(config.getString("permission-error"));
    }

    public String getItemsLimitExceededMessage() {
        return TextAdapter.colorize(config.getString("items-limit-exceeded"));
    }

    public String getXpLimitExceededMessage() {
        return TextAdapter.colorize(config.getString("xp-limit-exceeded"));
    }

    public String getSortedExtraItemsToRemoveMessage(int amount) {
        return TextAdapter.replaceSection(TextAdapter.colorize(config.getString("sort-items-extra")), amount, null);
    }
}
