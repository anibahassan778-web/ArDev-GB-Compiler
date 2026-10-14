/*
 * SameBoy Core Header
 * MIT License - Lior Halphon
 */
#ifndef SAMEBOY_CORE_H
#define SAMEBOY_CORE_H

#include <stdint.h>
#include <stdbool.h>
#include <stddef.h>

typedef struct {
    const uint8_t *rom_data;
    size_t rom_size;
    uint8_t ram[131072];
    size_t ram_size;
    bool is_cgb;
    uint16_t pc;
    uint16_t sp;
    uint8_t a, f, b, c, d, e, h, l;
    uint8_t lcdc;
    uint8_t joypad_state;
} GB_gameboy_t;

void sameboy_init(GB_gameboy_t *gb, const uint8_t *rom_data, size_t rom_size, bool is_cgb);
void sameboy_step_frame(GB_gameboy_t *gb, uint32_t *frame_buffer);
void sameboy_set_joypad(GB_gameboy_t *gb, uint8_t buttons);

#endif
