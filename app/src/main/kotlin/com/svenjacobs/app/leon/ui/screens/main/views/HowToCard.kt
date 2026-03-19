/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.svenjacobs.app.leon.ui.screens.main.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.ui.screens.main.model.ButtonConfig
import com.svenjacobs.app.leon.ui.screens.main.model.ImageConfig

@Composable
fun HowToCard(
	title: String,
	description: String,
	modifier: Modifier = Modifier,
	isCollapsible: Boolean = true,
	initiallyExpanded: Boolean = false,
	image: ImageConfig? = null,
	button: ButtonConfig? = null,
) {
	var expanded by remember { mutableStateOf(initiallyExpanded) }

	val cardModifier = modifier.then(
		if (isCollapsible) {
			Modifier.clickable { expanded = !expanded }
		} else Modifier,
	)

	Card(modifier = cardModifier) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier.weight(1f),
				)

				if (isCollapsible) {
					IconButton(onClick = { expanded = !expanded }) {
						Icon(
							imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
							contentDescription = stringResource(R.string.a11y_collapse),
						)
					}
				}
			}

			AnimatedVisibility(
				visible = expanded,
				enter = expandVertically(),
				exit = shrinkVertically(),
			) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					verticalArrangement = Arrangement.spacedBy(12.dp),
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(12.dp),
					) {
						image?.let {
							Image(
								modifier = Modifier
									.heightIn(max = 300.dp)
									.padding(bottom = 12.dp),
								painter = painterResource(it.imgId),
								contentDescription = it.contentDescription,
								contentScale = ContentScale.Fit,
							)
						}

						Text(
							text = description,
							style = MaterialTheme.typography.bodyMedium,
						)
					}

					button?.let {
						OutlinedButton(
							modifier = Modifier.fillMaxWidth(),
							onClick = button.onClick,
						) {
							Text(
								text = button.text,
								style = MaterialTheme.typography.bodyMedium,
							)
						}
					}

				}
			}
		}
	}
}
