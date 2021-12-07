package net.runelite.client.plugins.coxadditions.overlays;

import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.coxadditions.CoxAdditionsConfig;
import net.runelite.client.plugins.coxadditions.CoxAdditionsPlugin;
import net.runelite.client.plugins.coxadditions.utils.HealingPoolInfo;
import net.runelite.client.plugins.coxadditions.utils.ShamanInfo;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

@Singleton
public class CoxAdditionsOverlay extends Overlay {
	private final Client client;
	private final CoxAdditionsPlugin plugin;
	private final CoxAdditionsConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	private CoxAdditionsOverlay(Client client, CoxAdditionsPlugin plugin, CoxAdditionsConfig config, ModelOutlineRenderer modelOutlineRenderer) {
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
		this.setPosition(OverlayPosition.DYNAMIC);
		this.setPriority(OverlayPriority.HIGH);
		this.setLayer(OverlayLayer.ABOVE_SCENE);
	}

	public Dimension render(Graphics2D graphics) {
		if (this.client.getVar(Varbits.IN_RAID) == 1) {
			NPC mageHand;
			String text;
			Point pointShadow;
			Point p;
			Font oldFont;
			if (this.config.olmCrippleTimer() && this.plugin.handCripple && this.plugin.meleeHand != null) {
				mageHand = this.plugin.meleeHand;
				text = Integer.toString(this.plugin.crippleTimer);
				pointShadow = mageHand.getCanvasTextLocation(graphics, text, 50);
				if (pointShadow != null) {
					p = new Point(pointShadow.getX() + 1, pointShadow.getY() + 1);
					oldFont = graphics.getFont();
					graphics.setFont(new Font("Arial", 1, this.config.olmCrippleTextSize()));
					OverlayUtil.renderTextLocation(graphics, p, text, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, pointShadow, text, this.config.olmCrippleText());
					graphics.setFont(oldFont);
				}
			}

			Iterator var9;
			Polygon poly;
			if (this.config.shamanSlam() && this.plugin.shamanInfoList.size() > 0) {
				var9 = this.plugin.shamanInfoList.iterator();

				while (var9.hasNext()) {
					ShamanInfo sInfo = (ShamanInfo)var9.next();
					if (sInfo.jumping && sInfo.interactingLoc != null) {
						poly = Perspective.getCanvasTileAreaPoly(this.client, sInfo.interactingLoc, 3);
						this.renderArea(graphics, this.config.shamanSlamColor(), poly);
					}
				}
			}

			if (this.config.coxHerbTimer() != CoxAdditionsConfig.CoXHerbTimerMode.OFF && (this.plugin.coxHerb1 != null || this.plugin.coxHerb2 != null)) {
				if (this.config.coxHerbTimer() == CoxAdditionsConfig.CoXHerbTimerMode.TEXT) {
					GameObject herb;
					if (this.plugin.coxHerb1 != null) {
						herb = this.plugin.coxHerb1;
						text = Integer.toString(this.plugin.coxHerbTimer1);
						pointShadow = herb.getCanvasTextLocation(graphics, text, 50);
						p = new Point(pointShadow.getX() + 1, pointShadow.getY() + 1);
						oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", 1, this.config.coxHerbTimerSize()));
						OverlayUtil.renderTextLocation(graphics, p, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, this.config.coxHerbTimerColor());
						graphics.setFont(oldFont);
					}

					if (this.plugin.coxHerb2 != null) {
						herb = this.plugin.coxHerb2;
						text = Integer.toString(this.plugin.coxHerbTimer2);
						pointShadow = herb.getCanvasTextLocation(graphics, text, 50);
						p = new Point(pointShadow.getX() + 1, pointShadow.getY() + 1);
						oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", 1, this.config.coxHerbTimerSize()));
						OverlayUtil.renderTextLocation(graphics, p, text, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, pointShadow, text, this.config.coxHerbTimerColor());
						graphics.setFont(oldFont);
					}
				} else if (this.config.coxHerbTimer() == CoxAdditionsConfig.CoXHerbTimerMode.PIE) {
					Point position;
					ProgressPieComponent progressPie;
					Color colorFill;
					int ticks;
					double progress;
					if (this.plugin.coxHerb1 != null) {
						position = this.plugin.coxHerb1.getCanvasLocation(100);
						progressPie = new ProgressPieComponent();
						progressPie.setDiameter(this.config.coxHerbTimerSize());
						colorFill = new Color(this.config.coxHerbTimerColor().getRed(), this.config.coxHerbTimerColor().getGreen(), this.config.coxHerbTimerColor().getBlue(), 100);
						progressPie.setFill(colorFill);
						progressPie.setBorderColor(this.config.coxHerbTimerColor());
						progressPie.setPosition(position);
						ticks = 16 - this.plugin.coxHerbTimer1;
						progress = 1.0D - (double)ticks / 16.0D;
						progressPie.setProgress(progress);
						progressPie.render(graphics);
					}

					if (this.plugin.coxHerb2 != null) {
						position = this.plugin.coxHerb2.getCanvasLocation(100);
						progressPie = new ProgressPieComponent();
						progressPie.setDiameter(this.config.coxHerbTimerSize());
						colorFill = new Color(this.config.coxHerbTimerColor().getRed(), this.config.coxHerbTimerColor().getGreen(), this.config.coxHerbTimerColor().getBlue(), 100);
						progressPie.setFill(colorFill);
						progressPie.setBorderColor(this.config.coxHerbTimerColor());
						progressPie.setPosition(position);
						ticks = 16 - this.plugin.coxHerbTimer2;
						progress = 1.0D - (double)ticks / 16.0D;
						progressPie.setProgress(progress);
						progressPie.render(graphics);
					}
				}
			}

			String bossName;
			Point textLoc;
			if (this.config.olmHealingPoolTimer() != CoxAdditionsConfig.healingPoolMode.OFF && this.plugin.olmHealingPools.size() > 0) {
				HealingPoolInfo poolInfo;
				if (this.config.olmHealingPoolTimer() != CoxAdditionsConfig.healingPoolMode.TIMER && this.config.olmHealingPoolTimer() != CoxAdditionsConfig.healingPoolMode.BOTH) {
					if (this.config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.OVERLAY || this.config.olmHealingPoolTimer() == CoxAdditionsConfig.healingPoolMode.BOTH) {
						var9 = this.plugin.olmHealingPools.iterator();

						while (var9.hasNext()) {
							poolInfo = (HealingPoolInfo)var9.next();
							if (poolInfo.lp != null) {
								poly = Perspective.getCanvasTilePoly(this.client, poolInfo.lp);
								if (poly != null) {
									graphics.setColor(new Color(0, 255, 255, 255));
									graphics.setStroke(new BasicStroke(1.0F));
									graphics.draw(poly);
									graphics.setColor(new Color(0, 255, 255, 10));
									graphics.fill(poly);
								}
							}
						}
					}
				} else {
					var9 = this.plugin.olmHealingPools.iterator();

					while (var9.hasNext()) {
						poolInfo = (HealingPoolInfo)var9.next();
						bossName = String.valueOf(poolInfo.ticks);
						if (poolInfo.lp != null) {
							p = Perspective.getCanvasTextLocation(this.client, graphics, poolInfo.lp, bossName, 0);
							textLoc = new Point(p.getX() + 1, p.getY() + 1);
							oldFont = graphics.getFont();
							graphics.setFont(new Font("Arial", 1, 12));
							OverlayUtil.renderTextLocation(graphics, textLoc, bossName, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, p, bossName, this.config.olmHealingPoolTimerColor());
							graphics.setFont(oldFont);
						}
					}
				}
			}

			NPC meleeHand;
			if (this.config.vasaCrystalTimer() != CoxAdditionsConfig.crystalTimerMode.OFF && this.plugin.vasaCrystalTicks > 0) {
				var9 = this.client.getNpcs().iterator();

				while (var9.hasNext()) {
					meleeHand = (NPC)var9.next();
					if (meleeHand.getId() == 7568) {
						if (this.config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.BOLD) {
							graphics.setFont(FontManager.getRunescapeBoldFont());
						} else if (this.config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.REGULAR) {
							graphics.setFont(FontManager.getRunescapeFont());
						} else if (this.config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.SMALL) {
							graphics.setFont(FontManager.getRunescapeSmallFont());
						} else if (this.config.vasaCrystalTimer() == CoxAdditionsConfig.crystalTimerMode.CUSTOM) {
							graphics.setFont(new Font("Arial", 1, this.config.vasaCrystalTextSize()));
						}

						bossName = this.plugin.vasaAtCrystal ? "*" + this.plugin.vasaCrystalTicks : Integer.toString(this.plugin.vasaCrystalTicks);
						p = meleeHand.getCanvasTextLocation(graphics, bossName, meleeHand.getLogicalHeight() / 2);
						if (p != null) {
							textLoc = new Point(p.getX() + 1, p.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, textLoc, bossName, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, new Point(p.getX(), p.getY()), bossName, this.config.vasaCrystalTimerColor());
						}
					}
				}
			}

			if (this.config.chinRope() != CoxAdditionsConfig.chinRopeMode.OFF && this.plugin.ropeNpcs.size() > 0) {
				this.drawRopeChin(graphics);
			}

			Point meleeTextLoc;
			if (this.config.smallMuttaHp() && this.plugin.meatTreeAlive && this.plugin.smallMuttaAlive) {
				mageHand = this.plugin.smallMutta;
				Color textColor = Color.WHITE;
				bossName = "";
				if (mageHand.getHealthRatio() > 0 || this.plugin.lastRatio != 0 && this.plugin.lastHealthScale != 0) {
					if (mageHand.getHealthRatio() > 0) {
						this.plugin.lastRatio = mageHand.getHealthRatio();
						this.plugin.lastHealthScale = mageHand.getHealthScale();
					}

					float floatRatio = (float)this.plugin.lastRatio / (float)this.plugin.lastHealthScale * 100.0F;
					if (floatRatio > 75.0F) {
						textColor = Color.GREEN;
					} else if (floatRatio > 50.0F) {
						textColor = Color.YELLOW;
					} else {
						textColor = Color.RED;
					}

					bossName = Float.toString(floatRatio).substring(0, 4);
					textLoc = this.plugin.smallMutta.getCanvasTextLocation(graphics, bossName, 50);
					if (textLoc != null) {
						meleeTextLoc = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
						oldFont = graphics.getFont();
						graphics.setFont(new Font("Arial", 1, 15));
						OverlayUtil.renderTextLocation(graphics, meleeTextLoc, bossName, Color.BLACK);
						OverlayUtil.renderTextLocation(graphics, textLoc, bossName, textColor);
						graphics.setFont(oldFont);
					}
				}
			}

			Point point;
			if (this.config.meatTreeChopCycle() == CoxAdditionsConfig.meatTreeChopCycleMode.OVERLAY && this.plugin.startedChopping && this.plugin.meatTreeAlive && this.plugin.meatTree != null) {
				text = String.valueOf(this.plugin.ticksToChop);
				point = this.plugin.meatTree.getCanvasTextLocation(graphics, text, 0);
				if (point != null) {
					pointShadow = new Point(point.getX() + 1, point.getY() + 1);
					oldFont = graphics.getFont();
					graphics.setFont(new Font("Arial", 1, 15));
					OverlayUtil.renderTextLocation(graphics, pointShadow, text, Color.BLACK);
					OverlayUtil.renderTextLocation(graphics, point, text, Color.WHITE);
					graphics.setFont(oldFont);
				}
			}

			if (this.config.instanceTimer() == CoxAdditionsConfig.instanceTimerMode.OVERHEAD && this.plugin.isInstanceTimerRunning) {
				Player player = this.client.getLocalPlayer();
				if (player != null) {
					point = player.getCanvasTextLocation(graphics, "#", player.getLogicalHeight() + 60);
					if (point != null) {
						OverlayUtil.renderTextLocation(graphics, point, String.valueOf(this.plugin.instanceTimer), Color.CYAN);
					}
				}
			}

			Color meleeColor;
			if (this.config.olmPhaseHighlight() && this.plugin.olmSpawned && this.plugin.olmHead != null) {
				NPCComposition comp = this.plugin.olmHead.getComposition();
				int size = comp.getSize();
				LocalPoint lp = this.plugin.olmHead.getLocalLocation();
				if (lp != null) {
					Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
					if (tilePoly != null) {
						meleeColor = this.config.olmHighlightColor();
						if (this.plugin.olmPhase.equals("crystal")) {
							meleeColor = Color.MAGENTA;
						} else if (this.plugin.olmPhase.equals("acid")) {
							meleeColor = Color.GREEN;
						} else if (this.plugin.olmPhase.equals("flame")) {
							meleeColor = Color.RED;
						}

						this.renderPoly(graphics, meleeColor, tilePoly, this.config.olmThiCC());
					}
				}
			}

			if (!this.config.tlList().equals("")) {
				var9 = this.client.getNpcs().iterator();

				while (var9.hasNext()) {
					meleeHand = (NPC)var9.next();
					if (meleeHand.getName() != null && meleeHand.getId() != 8203) {
						bossName = "";
						if (meleeHand.getName().toLowerCase().contains("tekton")) {
							bossName = "tekton";
						} else if (meleeHand.getName().toLowerCase().contains("jewelled crab")) {
							bossName = "jewelled crab";
						} else {
							bossName = meleeHand.getName().toLowerCase();
						}

						if (this.plugin.tlList.contains(bossName)) {
							NPCComposition comp = meleeHand.getComposition();
							int size = comp.getSize();
							LocalPoint lp = LocalPoint.fromWorld(this.client, meleeHand.getWorldLocation());
							if (lp != null) {
								lp = new LocalPoint(lp.getX() + size * 128 / 2 - 64, lp.getY() + size * 128 / 2 - 64);
								Polygon tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, size);
								this.renderPoly(graphics, this.config.tlColor(), tilePoly, this.config.tlThiCC());
							}
						}
					}
				}
			}

			if (this.config.olmCrystals() != CoxAdditionsConfig.olmCrystalMode.OFF) {
				int index = 0;
				Iterator var41 = this.client.getGraphicsObjects().iterator();

				while (var41.hasNext()) {
					GraphicsObject obj = (GraphicsObject)var41.next();
					if (obj.getId() == 1447) {
						LocalPoint lp = new LocalPoint(obj.getLocation().getX() - 1, obj.getLocation().getY() - 1);
						Polygon tilePoly;
						if (this.config.olmCrystals() == CoxAdditionsConfig.olmCrystalMode.AREA) {
							tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, 3);
						} else {
							tilePoly = Perspective.getCanvasTileAreaPoly(this.client, lp, 1);
						}

						if (tilePoly != null) {
							if (this.config.olmRaveCrystals()) {
								this.renderPoly(graphics, (Color)this.plugin.olmRaveCrystalsList.get(index), tilePoly);
							} else {
								this.renderPoly(graphics, this.config.olmCrystalsColor(), tilePoly);
							}

							++index;
						}
					}
				}
			}

			if (this.config.olmTp()) {
				var9 = this.client.getGraphicsObjects().iterator();

				while (var9.hasNext()) {
					GraphicsObject obj = (GraphicsObject)var9.next();
					if (obj.getId() == 1359) {
						poly = Perspective.getCanvasTilePoly(this.client, obj.getLocation());
						this.renderTp(graphics, Color.ORANGE, poly);
						break;
					}
				}
			}

			if (this.config.olmHandsHealth() == CoxAdditionsConfig.olmHandsHealthMode.OVERLAY && (this.plugin.mageHand != null || this.plugin.meleeHand != null)) {
				mageHand = this.plugin.mageHand;
				meleeHand = this.plugin.meleeHand;
				oldFont = graphics.getFont();
				graphics.setFont(FontManager.getRunescapeBoldFont());
				Point meleePointShadow;
				String mageText;
				if (this.client.getVarbitValue(5424) == 1) {
					if (mageHand != null && this.plugin.mageHandHp >= 0) {
						mageText = String.valueOf(this.plugin.mageHandHp);
						meleeColor = Color.WHITE;
						if (this.plugin.mageHandHp < 100) {
							meleeColor = Color.RED;
						}

						meleeTextLoc = mageHand.getCanvasTextLocation(graphics, mageText, -75);
						if (meleeTextLoc != null) {
							meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, mageText, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, mageText, meleeColor);
						}
					}

					if (meleeHand != null && this.plugin.meleeHandHp >= 0) {
						mageText = String.valueOf(this.plugin.meleeHandHp);
						meleeColor = Color.WHITE;
						if (this.plugin.meleeHandHp < 100) {
							meleeColor = Color.RED;
						}

						meleeTextLoc = meleeHand.getCanvasTextLocation(graphics, mageText, -75);
						if (meleeTextLoc != null) {
							meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, mageText, Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, mageText, meleeColor);
						}
					}
				} else {
					float floatRatioMelee;
					if (mageHand != null) {
						mageText = "";
						meleeColor = Color.CYAN;
						if (mageHand.getHealthRatio() > 0 || this.plugin.mageHandLastRatio != 0 && this.plugin.mageHandLastHealthScale != 0) {
							if (mageHand.getHealthRatio() > 0) {
								System.out.println("Set mage hand ratio/scale");
								this.plugin.mageHandLastRatio = mageHand.getHealthRatio();
								this.plugin.mageHandLastHealthScale = mageHand.getHealthScale();
							}

							floatRatioMelee = (float)this.plugin.mageHandLastRatio / (float)this.plugin.mageHandLastHealthScale * 100.0F;
							if (floatRatioMelee <= 15.0F) {
								meleeColor = Color.RED;
							}

							mageText = Float.toString(floatRatioMelee);
							mageText = mageText.substring(0, mageText.indexOf("."));
						}

						meleeTextLoc = mageHand.getCanvasTextLocation(graphics, mageText, 0);
						if (meleeTextLoc != null) {
							meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, mageText + "%", Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, mageText + "%", meleeColor);
						}
					}

					if (meleeHand != null) {
						meleeColor = Color.WHITE;
						String meleeText = "";
						if (meleeHand.getHealthRatio() > 0 || this.plugin.meleeHandLastRatio != 0 && this.plugin.meleeHandLastHealthScale != 0) {
							if (this.plugin.meleeHand.getHealthRatio() > 0) {
								this.plugin.meleeHandLastRatio = meleeHand.getHealthRatio();
								this.plugin.meleeHandLastHealthScale = meleeHand.getHealthScale();
							}

							floatRatioMelee = (float)this.plugin.meleeHandLastRatio / (float)this.plugin.meleeHandLastHealthScale * 100.0F;
							if (floatRatioMelee <= 15.0F) {
								meleeColor = Color.RED;
							}

							meleeText = Float.toString(floatRatioMelee);
							meleeText = meleeText.substring(0, meleeText.indexOf("."));
						}

						meleeTextLoc = meleeHand.getCanvasTextLocation(graphics, meleeText, 0);
						if (meleeTextLoc != null) {
							meleePointShadow = new Point(meleeTextLoc.getX() + 1, meleeTextLoc.getY() + 1);
							OverlayUtil.renderTextLocation(graphics, meleePointShadow, meleeText + "%", Color.BLACK);
							OverlayUtil.renderTextLocation(graphics, meleeTextLoc, meleeText + "%", meleeColor);
						}
					}
				}

				graphics.setFont(oldFont);
			}
		}

		return null;
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon, int width) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke((float)width));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
			graphics.fill(polygon);
		}

	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2.0F));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
			graphics.fill(polygon);
		}

	}

	private void renderTp(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2.0F));
			graphics.draw(polygon);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
			graphics.fill(polygon);
		}

	}

	private void renderArea(Graphics2D graphics, Color color, Shape polygon) {
		if (polygon != null) {
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
			graphics.fill(polygon);
		}

	}

	public void drawRopeChin(Graphics2D graphics) {
		boolean highlight = false;
		Iterator var3 = this.plugin.ropeNpcs.iterator();

		while (var3.hasNext()) {
			NPC npc = (NPC)var3.next();
			WorldPoint wp = npc.getWorldLocation();
			Iterator var6 = this.plugin.ropeNpcs.iterator();

			while (var6.hasNext()) {
				NPC target = (NPC)var6.next();
				if (target != npc) {
					WorldPoint tWp = target.getWorldLocation();
					int x_dist = Math.abs(tWp.getX() - wp.getX());
					int y_dist = Math.abs(tWp.getY() - wp.getY());
					if (x_dist <= 1 && y_dist <= 1) {
						highlight = true;
					}
				}
			}

			if (highlight) {
				if (this.config.chinRope() == CoxAdditionsConfig.chinRopeMode.HULL) {
					Shape poly = npc.getConvexHull();
					if (poly != null) {
						graphics.setColor(new Color(this.config.chinRopeColor().getRed(), this.config.chinRopeColor().getGreen(), this.config.chinRopeColor().getBlue(), 255));
						graphics.setStroke(new BasicStroke((float)this.config.chinRopeThiCC()));
						graphics.draw(poly);
						graphics.setColor(new Color(this.config.chinRopeColor().getRed(), this.config.chinRopeColor().getGreen(), this.config.chinRopeColor().getBlue(), 0));
						graphics.fill(poly);
					}
				} else if (this.config.chinRope() == CoxAdditionsConfig.chinRopeMode.OUTLINE) {
					this.modelOutlineRenderer.drawOutline((NPC)npc, this.config.chinRopeThiCC(), this.config.chinRopeColor(), 2);
				}
			}
		}

	}

	protected void renderTextLocation(Graphics2D graphics, @Nullable Point txtLoc, @Nullable String text, @Nonnull Color color) {
		if (txtLoc != null && !Strings.isNullOrEmpty(text)) {
			int x = txtLoc.getX();
			int y = txtLoc.getY();
			graphics.setColor(Color.BLACK);
			graphics.drawString(text, x, y + 1);
			graphics.drawString(text, x, y - 1);
			graphics.drawString(text, x + 1, y);
			graphics.drawString(text, x - 1, y);
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
			graphics.drawString(text, x, y);
		}
	}

	protected void renderImageLocation(Graphics2D graphics, @Nullable Point imgLoc, @Nullable BufferedImage image) {
		if (imgLoc != null && image != null) {
			int x = imgLoc.getX();
			int y = imgLoc.getY();
			graphics.drawImage(image, x, y, (ImageObserver)null);
		}
	}
}
