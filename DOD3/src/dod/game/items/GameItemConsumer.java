package dod.game.items;

/**
 * An interface implemented by anything that can "consume an item". This allows
 * the item to act on the player, e.g. giving the player gold, hp and taking
 * away the AP.
 * 
 * Future items could do more, e.g. kill the player instantly.
 */
public interface GameItemConsumer {

    /**
     * Adds more gold to the amount of gold the player already has
     * 
     * @param gold
     *            The amount of gold to add
     */
    public abstract void addGold(int gold);

    /**
     * Add to the player's HP
     * 
     * @param hp
     *            The amount of HP to add to the player
     */
    public abstract void incrementHealth(int hp);

    /**
     * Sets the AP to zero. Used by actions which take up all the AP
     */
    public abstract void zeroAP();

}