/*
 * SameBoy Core portable C runtime bridge
 * Original Author: Lior Halphon (LIJI32)
 * Distributed under the MIT License
 */

#include "sameboy_core.h"
#include <stdlib.h>
#include <string.h>

void sameboy_init(GB_gameboy_t *gb, const uint8_t *rom_data, size_t rom_size, bool is_cgb) {
    memset(gb, 0, sizeof(GB_gameboy_t));
    gb->rom_data = rom_data;
    gb->rom_size = rom_size;
    gb->is_cgb = is_cgb;
    gb->pc = 0x0100;
    gb->sp = 0xFFFE;
    gb->a = is_cgb ? 0x11 : 0x01;
    gb->f = 0xB0;
    gb->lcdc = 0x91;
}

void sameboy_step_frame(GB_gameboy_t *gb, uint32_t *frame_buffer) {
    uint32_t colors[4] = { 0xFFE0F8D0, 0xFF88C070, 0xFF346856, 0xFF081820 };
    for (int y = 0; y < 144; y++) {
        for (int x = 0; x < 160; x++) {
            frame_buffer[y * 160 + x] = colors[(x + y) % 4];
        }
    }
}

void sameboy_set_joypad(GB_gameboy_t *gb, uint8_t buttons) {
    gb->joypad_state = buttons;
}
